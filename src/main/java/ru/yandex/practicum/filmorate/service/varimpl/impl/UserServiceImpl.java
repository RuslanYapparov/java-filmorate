package ru.yandex.practicum.filmorate.service.varimpl.impl;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import ru.yandex.practicum.filmorate.dao.FilmorateVariableStorageDao;
import ru.yandex.practicum.filmorate.dao.varimpl.FriendshipDao;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundInStorageException;
import ru.yandex.practicum.filmorate.exception.UserValidationException;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
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
    private Consumer<User> userFriendsSetFiller;

    public UserServiceImpl(@Qualifier("userRepository") FilmorateVariableStorageDao<UserEntity, User> objectDao,
                           FriendshipDao friendshipDao,
                           UserMapper userMapper) {
        super(objectDao);
        this.friendshipDao = friendshipDao;
        this.userMapper = userMapper;
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
    public List<User> getUsersFriendsSet(long userId) throws ObjectNotFoundInStorageException {
        super.getById(userId);    // Чтобы выбросилось исключение, если запрашивают друзей отсутствующего пользователя
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
                    .filter(fs ->  fs.getUserId() == currentUserId | fs.getFriendId() == currentUserId)
                    // В потоке оставляем только идентификаторы, не принадлежащие пользователю
                    .map(fs -> fs.getUserId() == currentUserId ? fs.getFriendId() : fs.getUserId())
                    .forEach(friendId -> user.getFriends().add(friendId));
        };
    }

}