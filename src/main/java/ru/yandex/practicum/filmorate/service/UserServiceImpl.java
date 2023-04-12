package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import ru.yandex.practicum.filmorate.exception.ObjectNotFoundInStorageException;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.UserModel;
import ru.yandex.practicum.filmorate.model.dto.restview.UserRestView;
import ru.yandex.practicum.filmorate.storage.InMemoryStorage;

@Service
@lombok.RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final InMemoryStorage<UserModel> users;
    private final UserMapper userMapper;

    @Override
    public List<UserRestView> addUserToAnotherUserFriendsSet(long userId, long friendId)
            throws ObjectNotFoundInStorageException {
        UserModel userModel = users.getById(userId);
        UserModel friend = users.getById(friendId);
        userModel.getFriends().add(friendId);
        friend.getFriends().add(userId);
        users.update(userModel);        // Думаю, если изменится способ хранения, нужно будет обновлять изменения в объектах
        users.update(friend);
        return userModel.getFriends().stream()
                .map(users::getById)
                .map(userMapper::toRestView)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserRestView> removeUserFromAnotherUserFriendsSet(long userId, long friendId)
            throws ObjectNotFoundInStorageException {
        UserModel userModel = users.getById(userId);
        UserModel friend = users.getById(friendId);
        userModel.getFriends().remove(friendId);
        friend.getFriends().remove(userId);
        users.update(userModel);
        users.update(friend);
        return userModel.getFriends().stream()
                .map(users::getById)
                .map(userMapper::toRestView)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserRestView> getUsersFriendsSet(long userId) throws ObjectNotFoundInStorageException {
        UserModel userModel = users.getById(userId);
        return userModel.getFriends().stream()
                .map(users::getById)
                .map(userMapper::toRestView)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserRestView> getCommonFriendsOfTwoUsers(long userId, long friendId)
            throws ObjectNotFoundInStorageException {
        UserModel userModel = users.getById(userId);
        UserModel friend = users.getById(friendId);
        return userModel.getFriends().stream()
                .filter(fiendId -> friend.getFriends().contains(fiendId))
                .map(users::getById)
                .map(userMapper::toRestView)
                .collect(Collectors.toList());
    }

}