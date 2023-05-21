package ru.yandex.practicum.filmorate.service.varimpl.impl;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import ru.yandex.practicum.filmorate.dao.FilmorateVariableStorageDao;
import ru.yandex.practicum.filmorate.dao.varimpl.FilmDirectorDao;
import ru.yandex.practicum.filmorate.dao.varimpl.FilmGenreDao;
import ru.yandex.practicum.filmorate.dao.varimpl.LikeDao;
import ru.yandex.practicum.filmorate.exception.BadRequestParameterException;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundInStorageException;
import ru.yandex.practicum.filmorate.mapper.DirectorMapper;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.data.FilmEntity;
import ru.yandex.practicum.filmorate.model.data.command.FilmGenreCommand;
import ru.yandex.practicum.filmorate.model.data.command.LikeCommand;
import ru.yandex.practicum.filmorate.model.service.*;
import ru.yandex.practicum.filmorate.model.presentation.restcommand.FilmRestCommand;
import ru.yandex.practicum.filmorate.service.varimpl.CrudServiceImpl;
import ru.yandex.practicum.filmorate.service.varimpl.FilmService;
import ru.yandex.practicum.filmorate.service.varimpl.UserService;

@Service
@Qualifier("filmService")
public class FilmServiceImpl extends CrudServiceImpl<Film, FilmEntity, FilmRestCommand> implements FilmService {
    @Qualifier("userService")
    private final UserService userService;
    @Qualifier("likeRepository")
    private final LikeDao likeDao;
    @Qualifier("filmGenreRepository")
    private final FilmGenreDao filmGenreDao;
    @Qualifier("filmDirectorRepository")
    private final FilmDirectorDao filmDirectorDao;
    private final FilmMapper filmMapper;
    private final DirectorMapper directorMapper;
    private final JdbcTemplate batchUpdater;

    public FilmServiceImpl(@Qualifier("filmRepository") FilmorateVariableStorageDao<FilmEntity, Film> objectDao,
                           UserService userService,
                           LikeDao likeDao,
                           FilmGenreDao filmGenreDao,
                           FilmDirectorDao filmDirectorDao,
                           FilmMapper filmMapper,
                           DirectorMapper directorMapper,
                           JdbcTemplate jdbcTemplate) {
        super(objectDao);
        this.userService = userService;
        this.likeDao = likeDao;
        this.filmGenreDao = filmGenreDao;
        this.filmDirectorDao = filmDirectorDao;
        this.filmMapper = filmMapper;
        this.directorMapper = directorMapper;
        this.batchUpdater = jdbcTemplate;
        this.objectFromDbEntityMapper = filmMapper::fromDbEntity;
        this.objectFromRestCommandMapper = filmMapper::fromRestCommand;
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
        List<Long> userIds = likeDao.getAllUsersIdsWhoLikedFilm(filmId);
        List<Genre> genres = filmGenreDao.getAllGenresOfFilmByFilmId(filmId);
        List<Director> directors = filmDirectorDao.getAllDirectorEntitiesByFilmId(filmId).stream()
                .map(directorMapper::fromDbEntity)
                .collect(Collectors.toList());
        FilmEntity filmEntity = objectDao.getById(filmId);
        Film film = objectFromDbEntityMapper.apply(filmEntity);
        film.getLikes().addAll(userIds);
        film.getGenres().addAll(genres);
        film.getDirectors().addAll(directors);
        return film;
    }

    @Override
    public List<Film> getAll() {
        Consumer<Film> filmGenresSetFiller = initializeFilmGenresSetFiller(filmGenreDao.getAll());
        Consumer<Film> filmLikesSetFiller = initializeFilmLikesSetFiller(likeDao.getAll());
        return objectDao.getAll().stream()
                .map(objectFromDbEntityMapper)
                .peek(filmLikesSetFiller)
                .peek(filmGenresSetFiller)
                .peek(film -> {
                    List<Director> directors = filmDirectorDao.getAllDirectorEntitiesByFilmId(film.getId()).stream()
                            .map(directorMapper::fromDbEntity)
                            .collect(Collectors.toList());
                    film.getDirectors().addAll(directors);
                })
                .collect(Collectors.toList());
    }

    @Override
    public Film update(FilmRestCommand filmRestCommand) throws ObjectNotFoundInStorageException {
        Film film = filmMapper.fromRestCommand(filmRestCommand);
        updateLikeStorages(film);
        updateGenreStorages(film);
        updateDirectorStorages(film);
        objectDao.update(film);
        return this.getById(filmRestCommand.getId());
    }

    @Override
    public Film deleteById(long filmId) {
        FilmEntity filmEntity = objectDao.deleteById(filmId, 0);
        return (filmMapper.fromDbEntity(filmEntity));
    }

    @Override
    public List<User> addLikeToFilmLikesSet(LikeCommand like) {
        likeDao.save(like);
        return this.getAllUsersWhoLikedFilm(like.getFilmId());
    }

    @Override
    public List<User> removeLikeFromFilmLikesSet(LikeCommand like) {
        likeDao.deleteById(like.getFilmId(), like.getUserId());
        return this.getAllUsersWhoLikedFilm(like.getFilmId());
    }

    @Override
    public List<User> getAllUsersWhoLikedFilm(long filmId) {
        List<Long> userIdsFromLikes = likeDao.getAllUsersIdsWhoLikedFilm(filmId);
        return userService.getAll().stream()
                .filter(user -> userIdsFromLikes.contains(user.getId()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Film> getMostLikedFilms(int count) {
        return this.getAll().stream()
                .sorted((film1, film2) -> film2.getLikes().size() - film1.getLikes().size())
                .limit(count)
                .collect(Collectors.toList());
    }

    @Override
    public List<Film> getAllFilmsLikedByUser(long userId) {
        List<Long> filmIdsFromLikes = likeDao.getAllFilmIdsLikedByUser(userId);
        return this.getAll().stream()
                .filter(film -> filmIdsFromLikes.contains(film.getId()))
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

            case "likes":
                return this.getAll().stream()
                        .filter(film -> filmIds.contains(film.getId()))
                        .sorted(Comparator.comparingInt(film -> film.getLikes().size()))
                        .collect(Collectors.toList());

            default:
                throw new BadRequestParameterException("Указан неверный параметер для сортировки: " + sortParameter);
        }
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
        batchUpdate("insert into film_genres (film_id, genre_id) values (?, ?)", filmId, genresToAdd);
        batchUpdate("delete from film_genres where film_id = ? and genre_id = ?", filmId, genresToDelete);
    }

    private void updateLikeStorages(Film film) {
        long filmId = film.getId();
        List<Long> oldLikesList = likeDao.getAllUsersIdsWhoLikedFilm(filmId);
        List<Long> newLikesList = new ArrayList<>(film.getLikes());
        List<Long> likesToDelete = oldLikesList.stream()
                .filter(userId -> !newLikesList.contains(userId))
                .collect(Collectors.toList());
        List<Long> likesToAdd = newLikesList.stream()
                .filter(userId -> !oldLikesList.contains(userId))
                .collect(Collectors.toList());
        batchUpdate("insert into likes (film_id, user_id) values (?, ?)", filmId, likesToAdd);
        batchUpdate("delete from likes where film_id = ? and user_id = ?", filmId, likesToDelete);
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
        batchUpdate("insert into film_directors (film_id, director_id) values (?, ?)", filmId, directorsToAdd);
        batchUpdate("delete from film_directors where film_id = ? and director_id = ?", filmId, directorsToDelete);
    }

    private Consumer<Film> initializeFilmLikesSetFiller(List<LikeCommand> likes) {
        return film -> {
            long currentFilmId = film.getId();
            likes.stream()
                    .filter(like -> like.getFilmId() == currentFilmId)
                    .map(LikeCommand::getUserId)
                    .forEach(userId -> film.getLikes().add(userId));
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