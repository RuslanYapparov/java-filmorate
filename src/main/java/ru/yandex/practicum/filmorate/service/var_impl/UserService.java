package ru.yandex.practicum.filmorate.service.var_impl;

import java.util.List;

import ru.yandex.practicum.filmorate.model.service.Event;
import ru.yandex.practicum.filmorate.model.service.FriendshipRequest;
import ru.yandex.practicum.filmorate.model.service.User;
import ru.yandex.practicum.filmorate.model.presentation.rest_command.UserRestCommand;
import ru.yandex.practicum.filmorate.service.CrudService;

public interface UserService extends CrudService<User, UserRestCommand> {

    List<User> addUserToAnotherUserFriendsSet(FriendshipRequest request);

    List<User> removeUserFromAnotherUserFriendsSet(FriendshipRequest request);

    List<User> getUsersFriendsSet(long userId);

    List<User> getCommonFriendsOfTwoUsers(long userId, long friendId);

    List<Event> getAllEventsByUserId(long userId);

}