package ru.yandex.practicum.filmorate.controller.servicecontrollers;

import ru.yandex.practicum.filmorate.model.controllercommandclasses.restcommand.impl.UserRestCommand;

import javax.validation.constraints.Positive;
import java.util.List;

public interface UserServiceController {

    List<UserRestCommand> getFriends(@Positive long id);

    List<UserRestCommand> getCommonFriends(@Positive long userId, @Positive long friendId);

    List<UserRestCommand> removeFromFriendsSet(@Positive long userId, @Positive long friendId);

    List<UserRestCommand> addToFriendsSet(@Positive long userId, @Positive long friendId);

}