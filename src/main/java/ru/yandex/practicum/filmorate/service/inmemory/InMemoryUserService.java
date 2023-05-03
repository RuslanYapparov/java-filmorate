package ru.yandex.practicum.filmorate.service.inmemory;

import java.util.List;

import ru.yandex.practicum.filmorate.model.presentation.restview.UserRestView;

public interface InMemoryUserService {

    List<UserRestView> addUserToAnotherUserFriendsSet(long userId, long friendId);

    List<UserRestView> removeUserFromAnotherUserFriendsSet(long userId, long friendId);

    List<UserRestView> getUsersFriendsSet(long userId);

    List<UserRestView> getCommonFriendsOfTwoUsers(long userId, long friendId);

}