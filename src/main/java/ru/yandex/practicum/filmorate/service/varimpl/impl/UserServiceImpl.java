package ru.yandex.practicum.filmorate.service.varimpl.impl;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import ru.yandex.practicum.filmorate.dao.FilmorateVariableStorageDao;
import ru.yandex.practicum.filmorate.dao.constimpl.FilmorateConstantStorageDaoImpl;
import ru.yandex.practicum.filmorate.dao.varimpl.FriendshipDao;
import ru.yandex.practicum.filmorate.dao.varimpl.impl.RecommendationsDaoIpl;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundInStorageException;
import ru.yandex.practicum.filmorate.exception.UserValidationException;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.data.FilmEntity;
import ru.yandex.practicum.filmorate.model.data.UserEntity;
import ru.yandex.practicum.filmorate.model.service.FriendshipRequest;
import ru.yandex.practicum.filmorate.model.service.User;
import ru.yandex.practicum.filmorate.model.presentation.restcommand.UserRestCommand;
import ru.yandex.practicum.filmorate.service.varimpl.CrudServiceImpl;
import ru.yandex.practicum.filmorate.service.varimpl.UserService;

@Service
@Qualifier("userService")
public class UserServiceImpl extends CrudServiceImpl<User, UserEntity, UserRestCommand> implements UserService {
    @Qualifier("friendshipRepository")
    private final FriendshipDao friendshipDao;
    private final UserMapper userMapper;

    @Qualifier("recommendationsDaoIpl")
    private final RecommendationsDaoIpl recommendationsDaoIpl;

    private final FilmorateConstantStorageDaoImpl<FilmEntity> filmorateConstantStorageDao;
    private Consumer<User> userFriendsSetFiller;

    public UserServiceImpl(@Qualifier("userRepository") FilmorateVariableStorageDao<UserEntity, User> objectDao,
                           FriendshipDao friendshipDao,
                           UserMapper userMapper, RecommendationsDaoIpl recommendationsDaoIpl, FilmorateConstantStorageDaoImpl<FilmEntity> filmorateConstantStorageDao) {
        super(objectDao);
        this.friendshipDao = friendshipDao;
        this.userMapper = userMapper;
        this.recommendationsDaoIpl = recommendationsDaoIpl;
        this.filmorateConstantStorageDao = filmorateConstantStorageDao;
        this.objectFromDbEntityMapper = this.userMapper::fromDbEntity;
        this.objectFromRestCommandMapper = this.userMapper::fromRestCommand;
    }

    @Override
    public User getById(long id) throws ObjectNotFoundInStorageException {
        User user = super.getById(id);
        userFriendsSetFiller = initializeUserFriendsSetFiller(friendshipDao.getAllByUserId(id));
        userFriendsSetFiller.accept(user);
        return user;
    }

    @Override
    public List<User> getAll() {
        userFriendsSetFiller = initializeUserFriendsSetFiller(friendshipDao.getAllFriendshipRequests());
        return super.getAll().stream()
                .peek(userFriendsSetFiller)
                .collect(Collectors.toList());
    }

    @Override
    public User update(UserRestCommand userRestCommand)
            throws UserValidationException, ObjectNotFoundInStorageException {
        User user = userMapper.fromRestCommand(userRestCommand);
        user = userMapper.fromDbEntity(objectDao.update(user));
        userFriendsSetFiller = initializeUserFriendsSetFiller(friendshipDao.getAllByUserId(user.getId()));
        userFriendsSetFiller.accept(user);
        return user;
    }

    @Override
    public User deleteById(long userId) throws ObjectNotFoundInStorageException {
        UserEntity userEntity = objectDao.deleteById(userId, 0);
        User user = userMapper.fromDbEntity(userEntity);
        userFriendsSetFiller = initializeUserFriendsSetFiller(friendshipDao.getAllByUserId(userId));
        userFriendsSetFiller.accept(user);
        return user;
    }

    @Override
    public List<User> getUsersFriendsSet(long userId) {
        List<Long> friendIds = friendshipDao.getAllByUserId(userId).stream()
                // Преобразуем поток дружб в поток идентификаторов друзей пользователя
                .map(fs -> fs.getUserId() == userId ? fs.getFriendId() : fs.getUserId())
                .collect(Collectors.toList());
        List<User> list = this.getAll();
        return list.stream()
                .filter(user -> friendIds.contains(user.getId()))
                .collect(Collectors.toList());
    }

    @Override
    public List<User> getCommonFriendsOfTwoUsers(long userId, long friendId) {
        List<FriendshipRequest> friendships = friendshipDao.getAllByTwoUserId(userId, friendId).stream()
                .filter(fs -> !((fs.getUserId() == userId & fs.getFriendId() == friendId) |       // Отсеиваем дружбу
                        (fs.getUserId() == friendId & fs.getFriendId() == userId)))          // Между пользователями,
                .collect(Collectors.toList());                                                 // Если она существует
        List<Long> friendIds = friendships.stream()
                // Преобразуем поток дружб в поток идентификаторов друзей пользователей
                .map(fs -> {
                    if (fs.getUserId() == userId | fs.getUserId() == friendId) {
                        return fs.getFriendId();
                    } else {
                        return fs.getUserId();
                    }
                })
                .collect(Collectors.toList());
        return this.getAll().stream()
                .filter(user -> friendIds.contains(user.getId()))
                .collect(Collectors.toList());
    }

    @Override
    public List<User> addUserToAnotherUserFriendsSet(FriendshipRequest request) {
        friendshipDao.save(request);
        return this.getUsersFriendsSet(request.getUserId());
    }

    @Override
    public List<User> removeUserFromAnotherUserFriendsSet(FriendshipRequest request) {
        friendshipDao.deleteById(request.getUserId(), request.getFriendId());
        return this.getUsersFriendsSet(request.getUserId());
    }

    private Consumer<User> initializeUserFriendsSetFiller(List<FriendshipRequest> friendships) {
        return user -> {                               // Заполняем HashSet с идентификаторами друзей пользователя
            long currentUserId = user.getId();
            friendships.stream()
                    // Отсеиваем все дружбы, в которых не фигурирует идентификатор пользователя
                    .filter(fs -> fs.getUserId() == currentUserId | fs.getFriendId() == currentUserId)
                    // В потоке оставляем только идентификаторы, не принадлежащие пользователю
                    .map(fs -> fs.getUserId() == currentUserId ? fs.getFriendId() : fs.getUserId())
                    .forEach(friendId -> user.getFriends().add(friendId));
        };
    }

    public List<FilmEntity> getRecommendationsFilms(long userId) {
        // проверям существует ли пользователь
        recommendationsDaoIpl.userExists(userId);

        List<FilmEntity> films = new ArrayList<>();

        if (recommendationsDaoIpl.numberLikes(userId) == 0) {
            // если нету понравившихся фильмов возвращаем рандомные 5
            return  getRandomFilms();
        }

        Set<Long> filmId = new HashSet<>();
        // ищем пользователей со схожими на 80% интересами
        List<Long> usersMatchingInterests = recommendationsDaoIpl.getUsersMatchingInterests(userId);
        if (usersMatchingInterests.size() != 0) {

            // получаем id фильмов которые они оценили а user нет
            for (Long id : usersMatchingInterests) {
              filmId.addAll(recommendationsDaoIpl.getFilmIdNonMatchingMovies(userId,id));
            }

            // получаем список рекомендованных фильмов
            for (Long id: filmId){
                films.add(filmorateConstantStorageDao.getById(id));
            }
            return films;
        }

        // список из 5 пользователей с самым большим совпадением по лайкам
        List<Long> users = recommendationsDaoIpl.getUsersIdCoincidencesInterests(userId);

        if (users.size() == 0){
            // если нет совпадений возвращаем 5 случайных фильмов
            return getRandomFilms();
        }

        // получаем id фильмов которые они оценили а user нет
        for (Long id : usersMatchingInterests) {
            filmId.addAll(recommendationsDaoIpl.getFilmIdNonMatchingMovies(userId,id));
        }

        // получаем список рекомендованных фильмов
        for (Long id: filmId){
            films.add(filmorateConstantStorageDao.getById(id));
        }
        return films;

        // логика:
        // если у пользователя 0 оценённых фильмов -> возвращаем 5 рандомных фильмов

        // количество совпадающих на 80% с user фильмов у других пользователей не 0 ->
        // -> возвращаем их фильмы которые user не оценивал

        // если количество совпадающих на 80% с user фильмов у других пользователей 0 ->
        // -> проверям 10 самых близких по интересам пользователей и возвращаем их фильмы которые user не оценивал

        // если не нашли ни одного близкого по интересам пользователя -> возвращаем 5 рандомных фильмов


    }

    private List<FilmEntity> getRandomFilms(){
        List<FilmEntity> films = new ArrayList<>();

        Set<Long> filmsId = recommendationsDaoIpl.getRandomFilm();

        for (Long filmId : filmsId) {
            films.add(filmorateConstantStorageDao.getById(filmId));
            // я не уверен что filmorateConstantStorageDao.getById(filmId) возвращает FilmEntity
            // но другого метода получения фильма по id я не нашёл
        }
        return films;
    }

}