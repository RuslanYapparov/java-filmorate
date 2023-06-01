package ru.yandex.practicum.filmorate.service.var_impl.impl;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import ru.yandex.practicum.filmorate.dao.FilmorateVariableStorageDao;
import ru.yandex.practicum.filmorate.dao.var_impl.FilmDirectorDao;
import ru.yandex.practicum.filmorate.dao.var_impl.FilmGenreDao;
import ru.yandex.practicum.filmorate.dao.var_impl.MarkDao;
import ru.yandex.practicum.filmorate.exception.BadRequestParameterException;
import ru.yandex.practicum.filmorate.exception.InternalLogicException;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundInStorageException;
import ru.yandex.practicum.filmorate.mapper.DirectorMapper;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.data.FilmEntity;
import ru.yandex.practicum.filmorate.model.data.command.FilmDirectorCommand;
import ru.yandex.practicum.filmorate.model.data.command.FilmGenreCommand;
import ru.yandex.practicum.filmorate.model.data.command.MarkCommand;
import ru.yandex.practicum.filmorate.model.presentation.rest_command.DirectorRestCommand;
import ru.yandex.practicum.filmorate.model.service.*;
import ru.yandex.practicum.filmorate.model.presentation.rest_command.FilmRestCommand;
import ru.yandex.practicum.filmorate.service.CrudService;
import ru.yandex.practicum.filmorate.service.var_impl.CrudServiceImpl;
import ru.yandex.practicum.filmorate.service.var_impl.EventService;
import ru.yandex.practicum.filmorate.service.var_impl.FilmService;
import ru.yandex.practicum.filmorate.service.var_impl.UserService;

@Service
public class FilmServiceImpl extends CrudServiceImpl<Film, FilmEntity, FilmRestCommand> implements FilmService {
    private final UserService userService;
    private final EventService eventService;
    private final CrudService<Director, DirectorRestCommand> directorService;
    private final MarkDao markDao;
    private final FilmGenreDao filmGenreDao;
    private final FilmDirectorDao filmDirectorDao;
    private final FilmMapper filmMapper;
    private final DirectorMapper directorMapper;
    private final JdbcTemplate batchUpdater;
    private final Comparator<Film> filmComparatorByRate;

    public FilmServiceImpl(FilmorateVariableStorageDao<FilmEntity, Film> objectDao,
                           UserService userService,
                           EventService eventService,
                           CrudService<Director, DirectorRestCommand> directorService,
                           MarkDao markDao,
                           FilmGenreDao filmGenreDao,
                           FilmDirectorDao filmDirectorDao,
                           FilmMapper filmMapper,
                           DirectorMapper directorMapper,
                           JdbcTemplate jdbcTemplate
                           ) {
        super(objectDao);
        this.userService = userService;
        this.eventService = eventService;
        this.directorService = directorService;
        this.markDao = markDao;
        this.filmGenreDao = filmGenreDao;
        this.filmDirectorDao = filmDirectorDao;
        this.filmMapper = filmMapper;
        this.directorMapper = directorMapper;
        this.batchUpdater = jdbcTemplate;
        this.objectFromDbEntityMapper = filmMapper::fromDbEntity;
        this.objectFromRestCommandMapper = filmMapper::fromRestCommand;
        this.filmComparatorByRate = Comparator.comparingDouble(film -> 10.0 - (double) film.getRate());
    }

    @Override
    public Film save(FilmRestCommand filmRestCommand) {
        Film film = super.save(filmRestCommand);
        if (filmRestCommand.getGenres() != null) {
            batchUpdate("insert into film_genres (film_id, genre_id) values (?, ?)",
                    film.getId(),
                    filmRestCommand.getGenres().stream()
                            .map(genre -> (long) genre.getId())
                            .collect(Collectors.toList()));
        }
        if (filmRestCommand.getDirectors() != null) {
            batchUpdate("insert into film_directors (film_id, director_id) values (?, ?)",
                    film.getId(),
                    filmRestCommand.getDirectors().stream()
                            .map(director -> (long) director.getId())
                            .collect(Collectors.toList()));
        }
        return this.getById(film.getId());
    }

    @Override
    public Film getById(long filmId) {
        List<Long> userIds = markDao.getAllUsersIdsWhoRatedFilm(filmId);
        List<Genre> genres = filmGenreDao.getAllGenresOfFilmByFilmId(filmId);
        List<Director> directors = filmDirectorDao.getAllDirectorEntitiesByFilmId(filmId).stream()
                .map(directorMapper::fromDbEntity)
                .collect(Collectors.toList());
        FilmEntity filmEntity = objectDao.getById(filmId);
        Film film = objectFromDbEntityMapper.apply(filmEntity);
        film.getMarksFrom().addAll(userIds);
        film.getGenres().addAll(genres);
        film.getDirectors().addAll(directors);
        return film;
    }

    @Override
    public List<Film> getAll() {
        Consumer<Film> filmGenresSetFiller = initializeFilmGenresSetFiller(filmGenreDao.getAll());
        Consumer<Film> filmMarksSetFiller = initializeFilmMarksSetFiller(markDao.getAll());
        Consumer<Film> filmDirectorsSetFiller = initializeFilmDirectorsSetFiller(filmDirectorDao.getAll(),
                                                                                 directorService.getAll());
        return objectDao.getAll().stream()
                .map(objectFromDbEntityMapper)
                .peek(filmMarksSetFiller)
                .peek(filmGenresSetFiller)
                .peek(filmDirectorsSetFiller)
                .collect(Collectors.toList());
    }

    @Override
    public List<Film> getMostLikedFilmsBySearch(String keyWord, String parameter) {

        switch (parameter) {
            case "title":
                return this.getAll().stream()
                        .filter(film -> film.getName().toLowerCase().contains(keyWord.toLowerCase()))
                        .sorted(filmComparatorByRate)
                        .collect(Collectors.toList());

            case "director":
                return directorService.getAll().stream()
                        .filter(director -> director.getName().toLowerCase().contains(keyWord.toLowerCase()))
                        .flatMap(director -> this.getAllFilmsByDirectorIdSortedBySomeParameter(director.getId(),
                                "marks").stream())
                        .collect(Collectors.toList());

            case "title,director":
            case "director,title":
                List<Film> films = this.getMostLikedFilmsBySearch(keyWord, "title");
                films.addAll(this.getMostLikedFilmsBySearch(keyWord, "director"));
                return films.stream()
                        .sorted(filmComparatorByRate)
                        .collect(Collectors.toList());

            default:
                throw new BadRequestParameterException("Указан неверный параметр для поиска: " + parameter);
        }
    }

    @Override
    public Film update(FilmRestCommand filmRestCommand) throws ObjectNotFoundInStorageException {
        Film film = filmMapper.fromRestCommand(filmRestCommand);
        objectDao.update(film);
        updateGenreStorages(film);
        updateDirectorStorages(film);
        return this.getById(filmRestCommand.getId());
    }

    @Override
    public Film deleteById(long filmId) {
        FilmEntity filmEntity = objectDao.deleteById(filmId, 0);
        return (filmMapper.fromDbEntity(filmEntity));
    }

    @Override
    public List<User> addMarkToFilm(MarkCommand markCommand) {
        markDao.save(markCommand);
        eventService.save(markCommand.getUserId(), EventType.MARK, EventOperation.ADD, markCommand.getFilmId());
        return this.getAllUsersWhoRatedFilm(markCommand.getFilmId());
    }

    @Override
    public List<User> removeMarkFromFilm(MarkCommand markCommand) {
        markDao.deleteById(markCommand.getFilmId(), markCommand.getUserId());
        eventService.save(markCommand.getUserId(), EventType.MARK, EventOperation.REMOVE, markCommand.getFilmId());
        return this.getAllUsersWhoRatedFilm(markCommand.getFilmId());
    }

    @Override
    public List<User> getAllUsersWhoRatedFilm(long filmId) {
        List<Long> userIdsFromMarks = markDao.getAllUsersIdsWhoRatedFilm(filmId);
        return userService.getAll().stream()
                .filter(user -> userIdsFromMarks.contains(user.getId()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Film> getMostLikedFilmsWithFilters(int count, int genreId, int year) {
        return this.getAll().stream()
                .filter(film -> genreId == 7777 || film.getGenres().contains(Genre.getGenreById(genreId)))
                .filter(film -> year == 7777 || film.getReleaseDate().getYear() == year)
                .sorted(filmComparatorByRate)
                .limit(count)
                .collect(Collectors.toList());
    }

    @Override
    public List<Film> getAllFilmsRatedByUser(long userId) {
        List<Long> filmIdsFromMarks = markDao.getAllFilmIdsRatedByUser(userId);
        return this.getAll().stream()
                .filter(film -> filmIdsFromMarks.contains(film.getId()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Genre> addFilmGenreAssociation(FilmGenreCommand filmGenre) {
        filmGenreDao.save(filmGenre);
        return filmGenreDao.getAllGenresOfFilmByFilmId(filmGenre.getFilmId());
    }

    @Override
    public List<Genre> removeFilmGenreAssociation(FilmGenreCommand filmGenre) {
        filmGenreDao.deleteById(filmGenre.getFilmId(), filmGenre.getGenreId());
        return filmGenreDao.getAllGenresOfFilmByFilmId(filmGenre.getFilmId());
    }

    @Override
    public List<Film> getAllFilmsByGenre(Genre genre) {
        List<Long> filmIdsFromFilmGenres = filmGenreDao.getAllFilmIdsByGenre(genre);
        return this.getAll().stream()
                .filter(film -> filmIdsFromFilmGenres.contains(film.getId()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Genre> getFilmGenresByFilmId(long filmId) {
        return filmGenreDao.getAllGenresOfFilmByFilmId(filmId);
    }

    @Override
    public List<Film> getAllFilmsByRatingMpa(RatingMpa ratingMpa) {
        return this.getAll().stream()
                .filter(film -> film.getRating() == ratingMpa)
                .collect(Collectors.toList());
    }

    @Override
    public List<Film> getAllFilmsByDirectorIdSortedBySomeParameter(int id, String sortParameter) {
        List<Long> filmIds = filmDirectorDao.getAllFilmIdsByDirectorId(id);
        switch (sortParameter) {
            case "year":
                return this.getAll().stream()
                        .filter(film -> filmIds.contains(film.getId()))
                        .sorted(Comparator.comparing(film -> film.getReleaseDate().getYear()))
                        .collect(Collectors.toList());

            case "marks":
                return this.getAll().stream()
                        .filter(film -> filmIds.contains(film.getId()))
                        .sorted(filmComparatorByRate)
                        .collect(Collectors.toList());

            default:
                throw new BadRequestParameterException("Указан неверный параметер для сортировки: " + sortParameter);
        }
    }

    @Override
    public List<Film> getRecommendedFilmsForUser(long userId) {
        userService.getById(userId);              // Проверка существования пользователя с указанным id в базе данных
        final int numberOfUsersWithSimilarPreferencesForReturnedValue = 5;      // В задании не указаны параметры для
        final int countOfFilmsInReturnedList = 10; // Рекоммендаций, но мне кажется они нужны, чтобы ограничить выборку
        Map<Long, List<Long>> goodMarksData = new HashMap<>();                                // Логика работы метода:
        List<Long> filmIdLikedByUser = markDao.getAllIdsOfFilmsWithPositiveMarkByUserId(userId);          // Получаем список фильмов
                                                                            // С положительными оценками пользователя
        markDao.getAll().stream()                                       // Получаем список всех оценок из базы данных
                .filter(markCommand -> markCommand.getRating() > 5)  // Отсеиваем только хорошие оценки пользователей
                .forEach(markCommand -> {                                                           // И заполняем
            long markUserId = markCommand.getUserId();     // Мапу лайков, где ключ - id пользователя, а значение -
            long ratedFilmId = markCommand.getFilmId();   // Список всех фильмов, которым этот пользователь поставил
            if (goodMarksData.containsKey(markUserId)) {                                            // Хорошую оценку
                goodMarksData.get(markUserId).add(ratedFilmId);
            } else {
                List<Long> ratedFilmIds = new ArrayList<>();
                ratedFilmIds.add(ratedFilmId);
                goodMarksData.put(markUserId, ratedFilmIds);
            }
        });
        goodMarksData.remove(userId);  // Удаляем из мапы данные самого пользователя, чтобы они не участвовали в логике

        Map<Long, List<Long>> sortedMarkData = new TreeMap<>(Comparator.comparingLong(markUserId ->   // Далее создаем
                goodMarksData.get(markUserId).stream()           // Сортирующую мапу, которая будет сортировать все id
                        .filter(filmIdLikedByUser::contains)  // пользователей по количеству лайков, совпавших с
                        .count()));                               // Пользователем, для которого ищем рекомендации
        sortedMarkData.putAll(goodMarksData);          // Закидываем в сортирующую мапу общую мапу со всеми оценками
        List<Long> recommendedFilmsIds = sortedMarkData.values().stream()      // Получаем рекоммендованные фильмы:
                .limit(numberOfUsersWithSimilarPreferencesForReturnedValue) // Берем первые несколько entry-значений
                .flatMap(Collection::stream)                                   // Преобразуем их в стрим id фильмов
                .filter(recommendedFilmId -> !filmIdLikedByUser.contains(recommendedFilmId))  // Убираем те, которые
                .limit(countOfFilmsInReturnedList)        // Хорошо оценил пользователь, которому ищем рекомендации
                .collect(Collectors.toList());
        return this.getAll().stream()                                                  // Берем список всех фильмов,
                .filter(film -> recommendedFilmsIds.contains(film.getId()))         // Убираем те, которые не в списке
                .sorted(filmComparatorByRate)                          // Рекомендованных, сортируем по оценкам фильма
                .collect(Collectors.toList());
    }

    public List<Film> getCommonFilmsOfTwoUsers(long userId, long friendId) {
        List<Film> filmsRatedByFirstUser = this.getAllFilmsRatedByUser(userId);
        List<Film> filmsRatedBySecondUser = this.getAllFilmsRatedByUser(friendId);

        return filmsRatedByFirstUser.stream()
                .filter(filmsRatedBySecondUser::contains)
                .sorted(filmComparatorByRate)
                .collect(Collectors.toList());
    }

    private void updateGenreStorages(Film film) {
        long filmId = film.getId();
        List<Genre> oldGenresList = filmGenreDao.getAllGenresOfFilmByFilmId(filmId);
        List<Genre> newGenreslist = new ArrayList<>(film.getGenres());
        List<Long> genresToDelete = oldGenresList.stream()
                .filter(genre -> !newGenreslist.contains(genre))
                .map(genre -> (long) genre.getId())
                .collect(Collectors.toList());
        List<Long> genresToAdd = newGenreslist.stream()
                .filter(genre -> !oldGenresList.contains(genre))
                .map(genre -> (long) genre.getId())
                .collect(Collectors.toList());
        batchUpdate("delete from film_genres where film_id = ? and genre_id = ?", filmId, genresToDelete);
        batchUpdate("insert into film_genres (film_id, genre_id) values (?, ?)", filmId, genresToAdd);
    }

    private void updateDirectorStorages(Film film) {
        long filmId = film.getId();
        List<Director> oldDirectorsList = filmDirectorDao.getAllDirectorEntitiesByFilmId(filmId).stream()
                .map(directorMapper::fromDbEntity)
                .collect(Collectors.toList());
        List<Director> newDirectorslist = new ArrayList<>(film.getDirectors());
        List<Long> directorsToDelete = oldDirectorsList.stream()
                .filter(director -> !newDirectorslist.contains(director))
                .map(director -> (long) director.getId())
                .collect(Collectors.toList());
        List<Long> directorsToAdd = newDirectorslist.stream()
                .filter(director -> !oldDirectorsList.contains(director))
                .map(director -> (long) director.getId())
                .collect(Collectors.toList());
        batchUpdate("delete from film_directors where film_id = ? and director_id = ?", filmId, directorsToDelete);
        batchUpdate("insert into film_directors (film_id, director_id) values (?, ?)", filmId, directorsToAdd);
    }

    private Consumer<Film> initializeFilmMarksSetFiller(List<MarkCommand> markCommands) {
        return film -> {
            long currentFilmId = film.getId();
            markCommands.stream()
                    .filter(markCommand -> markCommand.getFilmId() == currentFilmId)
                    .map(MarkCommand::getUserId)
                    .forEach(userId -> film.getMarksFrom().add(userId));
        };
    }

    private Consumer<Film> initializeFilmGenresSetFiller(List<FilmGenreCommand> filmGenres) {
        return film -> {
            long currentFilmId = film.getId();
            filmGenres.stream()
                    .filter(fgc -> fgc.getFilmId() == currentFilmId)
                    .map(FilmGenreCommand::getGenreId)
                    .map(Genre::getGenreById)
                    .forEach(genre -> film.getGenres().add(genre));
        };
    }

    private Consumer<Film> initializeFilmDirectorsSetFiller(List<FilmDirectorCommand> filmDirectorCommands,
                                                            List<Director> directors) {
        return film -> {
            long currentFilmId = film.getId();
            filmDirectorCommands.stream()
                    .filter(fdc -> fdc.getFilmId() == currentFilmId)
                    .map(FilmDirectorCommand::getDirectorId)
                    .map(directorId -> directors.stream()
                            .filter(director -> director.getId() == directorId)
                            .findFirst().orElseThrow(() -> new InternalLogicException("Внутрення ошибка приложения," +
                            "связанная с поиском режиссёра по идентификатору при заполнении соответствующего поля " +
                            "объекта film. Если Вы видите данное сообщение, пожалуйста, сообщите разработчикам")))
                    .forEach(director -> film.getDirectors().add(director));
        };
    }

    private void batchUpdate(String sql, long filmId, List<Long> longs) {
        if (!longs.isEmpty()) {
            batchUpdater.batchUpdate(sql, new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    ps.setLong(1, filmId);
                    ps.setLong(2, longs.get(i));
                }

                @Override
                public int getBatchSize() {
                    return longs.size();
                }

            });
        }
    }

}