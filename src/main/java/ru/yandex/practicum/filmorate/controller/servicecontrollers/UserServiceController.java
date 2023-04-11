package ru.yandex.practicum.filmorate.controller.servicecontrollers;

import ru.yandex.practicum.filmorate.model.restinteractionmodel.restview.UserRestView;

import javax.validation.constraints.Positive;
import java.util.List;

public interface UserServiceController {

    List<UserRestView> getFriends(@Positive long id);

    List<UserRestView> getCommonFriends(@Positive long userId, @Positive long friendId);

    List<UserRestView> removeFromFriendsSet(@Positive long userId, @Positive long friendId);

    List<UserRestView> addToFriendsSet(@Positive long userId, @Positive long friendId);

}