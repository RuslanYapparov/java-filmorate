package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import ru.yandex.practicum.filmorate.exception.ObjectNotFoundInStorageException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.restinteractionmodel.restview.UserRestView;
import ru.yandex.practicum.filmorate.storage.InMemoryStorage;
import ru.yandex.practicum.filmorate.util.UserObjectConverter;

@Service
@lombok.RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final InMemoryStorage<User> users;

    @Override
    public List<UserRestView> addUserToAnotherUserFriendsSet(long userId, long friendId)
            throws ObjectNotFoundInStorageException {
        User user = users.getById(userId);
        User friend = users.getById(friendId);
        user.getFriends().add(friendId);
        friend.getFriends().add(userId);
        users.update(user);        // Думаю, если изменится способ хранения, нужно будет обновлять изменения в объектах
        users.update(friend);
        return user.getFriends().stream()
                .map(users::getById)
                .map(UserObjectConverter::toRestView)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserRestView> removeUserFromAnotherUserFriendsSet(long userId, long friendId)
            throws ObjectNotFoundInStorageException {
        User user = users.getById(userId);
        User friend = users.getById(friendId);
        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);
        users.update(user);
        users.update(friend);
        return user.getFriends().stream()
                .map(users::getById)
                .map(UserObjectConverter::toRestView)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserRestView> getUsersFriendsSet(long userId) throws ObjectNotFoundInStorageException {
        User user = users.getById(userId);
        return user.getFriends().stream()
                .map(users::getById)
                .map(UserObjectConverter::toRestView)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserRestView> getCommonFriendsOfTwoUsers(long userId, long friendId)
            throws ObjectNotFoundInStorageException {
        User user = users.getById(userId);
        User friend = users.getById(friendId);
        return user.getFriends().stream()
                .filter(fiendId -> friend.getFriends().contains(fiendId))
                .map(users::getById)
                .map(UserObjectConverter::toRestView)
                .collect(Collectors.toList());
    }

}