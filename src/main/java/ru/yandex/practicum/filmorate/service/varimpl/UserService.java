package ru.yandex.practicum.filmorate.service.varimpl;

import java.util.List;

import ru.yandex.practicum.filmorate.model.service.FriendshipRequest;
import ru.yandex.practicum.filmorate.model.service.User;
import ru.yandex.practicum.filmorate.model.presentation.restcommand.UserRestCommand;
import ru.yandex.practicum.filmorate.service.CrudService;

public interface UserService extends CrudService<User, UserRestCommand> {

    List<User> addUserToAnotherUserFriendsSet(FriendshipRequest request);

    List<User> removeUserFromAnotherUserFriendsSet(FriendshipRequest request);

    List<User> getUsersFriendsSet(long userId);

    List<User> getCommonFriendsOfTwoUsers(long userId, long friendId);

}