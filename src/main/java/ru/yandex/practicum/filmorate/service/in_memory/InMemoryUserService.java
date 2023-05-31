package ru.yandex.practicum.filmorate.service.in_memory;

import java.util.List;

import ru.yandex.practicum.filmorate.model.presentation.rest_view.UserRestView;

public interface InMemoryUserService {

    List<UserRestView> addUserToAnotherUserFriendsSet(long userId, long friendId);

    List<UserRestView> removeUserFromAnotherUserFriendsSet(long userId, long friendId);

    List<UserRestView> getUsersFriendsSet(long userId);

    List<UserRestView> getCommonFriendsOfTwoUsers(long userId, long friendId);

}