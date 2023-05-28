package ru.yandex.practicum.filmorate.service.varimpl.impl;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import ru.yandex.practicum.filmorate.dao.FilmorateVariableStorageDao;
import ru.yandex.practicum.filmorate.dao.constimpl.FilmorateConstantStorageDaoImpl;
import ru.yandex.practicum.filmorate.dao.varimpl.FriendshipDao;
import ru.yandex.practicum.filmorate.dao.varimpl.LikeDao;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundInStorageException;
import ru.yandex.practicum.filmorate.exception.UserValidationException;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.data.FilmEntity;
import ru.yandex.practicum.filmorate.model.data.UserEntity;
import ru.yandex.practicum.filmorate.model.service.Film;
import ru.yandex.practicum.filmorate.model.service.FriendshipRequest;
import ru.yandex.practicum.filmorate.model.service.User;
import ru.yandex.practicum.filmorate.model.presentation.restcommand.UserRestCommand;
import ru.yandex.practicum.filmorate.service.varimpl.CrudServiceImpl;
import ru.yandex.practicum.filmorate.service.varimpl.FilmService;
import ru.yandex.practicum.filmorate.service.varimpl.UserService;

@Service
@Qualifier("userService")
public class UserServiceImpl extends CrudServiceImpl<User, UserEntity, UserRestCommand> implements UserService {
    @Qualifier("friendshipRepository")
    private final FriendshipDao friendshipDao;
    private final UserMapper userMapper;

    @Qualifier("filmService")
    private final FilmService filmService;

    @Qualifier("likeRepository")
    private final LikeDao likeDao;

    private final FilmorateConstantStorageDaoImpl<FilmEntity> filmorateConstantStorageDao;
    private Consumer<User> userFriendsSetFiller;

    public UserServiceImpl(@Qualifier("userRepository") FilmorateVariableStorageDao<UserEntity, User> objectDao,
                           FriendshipDao friendshipDao,
                           UserMapper userMapper, FilmService filmService, LikeDao likeDao, FilmorateConstantStorageDaoImpl<FilmEntity> filmorateConstantStorageDao) {
        super(objectDao);
        this.friendshipDao = friendshipDao;
        this.userMapper = userMapper;
        this.filmService = filmService;
        this.likeDao = likeDao;
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

    @Override
    public List<Film> getRecommendationsFilms(long userId) {

        this.getById(userId);              // Проверка существования пользователя с указанным id в базе данных

        final int numberOfUsersWithSimilarPreferencesForReturnedValue = 5;      // В задании не указаны параметры для
        final int countOfFilmsInReturnedList = 10; // Рекоммендаций, но мне кажется они нужны, чтобы ограничить выборку

        Map<Long, List<Long>> likeData = new HashMap<>();                                     // Логика работы метода:
        List<Long> filmIdsLikedByUser = likeDao.getAllFilmIdsLikedByUser(userId);  // Получаем список фильмов с лайками

        likeDao.getAll().forEach(likeCommand -> {          // Получаем список всех лайков из базы данных и заполняем
            long likeUserId = likeCommand.getUserId();     // Мапу лайков, где ключ - id пользователя, а значение -
            long likedFilmId = likeCommand.getFilmId(); // Список всех фильмов, которым этот пользователь поставил лайк
            if (likeData.containsKey(likeUserId)) {
                likeData.get(likeUserId).add(likedFilmId);
            } else {
                List<Long> likeFilmIds = new ArrayList<>();
                likeFilmIds.add(likedFilmId);
                likeData.put(likeUserId, likeFilmIds);
            }
        });
        likeData.remove(userId);         // Удаляем из мапы данные самого пользователя, чтобы они участвовали в логике

        Map<Long, List<Long>> sortedLikeData = new TreeMap<>(Comparator.comparingLong(likeUserId ->   // Далее создаем
                likeData.get(likeUserId).stream()              // Сортирующую мапу, которая будет сортировать все id
                        .filter(filmIdsLikedByUser::contains)  // пользователей по количеству лайков, совпавших с
                        .count()));                               // Пользователем, для которого ищем рекомендации

        sortedLikeData.putAll(likeData);  // Закидываем в сортирующую мапу общую мапу со всеми ползователями и лайками

        List<Long> recommendedFilmsIds = sortedLikeData.values().stream()      // Получаем рекоммендованные фильмы:
                .limit(numberOfUsersWithSimilarPreferencesForReturnedValue) // Берем первые несколько entry-значений
                .flatMap(Collection::stream)                                   // Преобразуем их в стрим id фильмов
                .filter(recommendedFilmId -> !filmIdsLikedByUser.contains(recommendedFilmId))  // Убираем те, которые
                .limit(countOfFilmsInReturnedList)               // Лайкнул пользователь, которому ищем рекомендации
                .collect(Collectors.toList());

        return filmService.getAll().stream()                                                  // Берем список всех фильмов,
                .filter(film -> recommendedFilmsIds.contains(film.getId()))         // Убираем те, которые не в списке
                .sorted((film1, film2) -> film2.getLikes().size() - film1.getLikes().size())     // Рекомендованных
                .collect(Collectors.toList());                           // Сортируем по количеству лайков у фильма
    }

}