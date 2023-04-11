package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.restinteractionmodel.restview.UserRestView;

import java.util.List;

public interface UserService {

    List<UserRestView> addUserToAnotherUserFriendsSet(long userId, long friendId);

    List<UserRestView> removeUserFromAnotherUserFriendsSet(long userId, long friendId);

    List<UserRestView> getUsersFriendsSet(long userId);

    List<UserRestView> getCommonFriendsOfTwoUsers(long userId, long friendId);

}