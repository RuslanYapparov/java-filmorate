package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import ru.yandex.practicum.filmorate.model.controllercommandclasses.restcommand.impl.UserRestCommand;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundInStorageException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryStorage;

@Service
@lombok.RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final InMemoryStorage<User> users;

    @Override
    public List<UserRestCommand> addUserToAnotherUserFriendsSet(long userId, long friendId)
            throws ObjectNotFoundInStorageException {
        User user = users.getById(userId);
        User friend = users.getById(friendId);
        user.getFriends().add(friendId);
        friend.getFriends().add(userId);
        users.update(user);        // Думаю, если изменится способ хранения, нужно будет обновлять изменения в объектах
        users.update(friend);
        return user.getFriends().stream()
                .map(users::getById)
                .map(UserRestCommand::new)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserRestCommand> removeUserFromAnotherUserFriendsSet(long userId, long friendId)
            throws ObjectNotFoundInStorageException {
        User user = users.getById(userId);
        User friend = users.getById(friendId);
        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);
        users.update(user);
        users.update(friend);
        return user.getFriends().stream()
                .map(users::getById)
                .map(UserRestCommand::new)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserRestCommand> getUsersFriendsSet(long userId) throws ObjectNotFoundInStorageException {
        User user = users.getById(userId);
        return user.getFriends().stream()
                .map(users::getById)
                .map(UserRestCommand::new)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserRestCommand> getCommonFriendsOfTwoUsers(long userId, long friendId)
            throws ObjectNotFoundInStorageException {
        User user = users.getById(userId);
        User friend = users.getById(friendId);
        return user.getFriends().stream()
                .filter(id -> friend.getFriends().contains(id))
                .map(users::getById)
                .map(UserRestCommand::new)
                .collect(Collectors.toList());
    }

}