package ru.yandex.practicum.filmorate.service.in_memory;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import ru.yandex.practicum.filmorate.exception.ObjectNotFoundInStorageException;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.service.User;
import ru.yandex.practicum.filmorate.model.presentation.rest_view.UserRestView;
import ru.yandex.practicum.filmorate.storage.InMemoryStorage;

@Service
@RequiredArgsConstructor
public class InMemoryUserServiceImpl implements InMemoryUserService {
    private final InMemoryStorage<User> users;
    private final UserMapper userMapper;

    @Override
    public List<UserRestView> addUserToAnotherUserFriendsSet(long userId, long friendId)
            throws ObjectNotFoundInStorageException {
        User user = users.getById(userId);
        User friend = users.getById(friendId);
        user.getFriends().add(friendId);
        friend.getFriends().add(userId);
        users.update(user);
        users.update(friend);
        return user.getFriends().stream()
                .map(users::getById)
                .map(userMapper::toRestView)
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
                .map(userMapper::toRestView)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserRestView> getUsersFriendsSet(long userId) throws ObjectNotFoundInStorageException {
        User user = users.getById(userId);
        return user.getFriends().stream()
                .map(users::getById)
                .map(userMapper::toRestView)
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
                .map(userMapper::toRestView)
                .collect(Collectors.toList());
    }

}