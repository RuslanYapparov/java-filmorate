package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.controllercommandclasses.restcommand.impl.UserRestCommand;

import java.util.List;

public interface UserService {

    List<UserRestCommand> addUserToAnotherUserFriendsSet(long userId, long friendId);

    List<UserRestCommand> removeUserFromAnotherUserFriendsSet(long userId, long friendId);

    List<UserRestCommand> getUsersFriendsSet(long userId);

    List<UserRestCommand> getCommonFriendsOfTwoUsers(long userId, long friendId);

}