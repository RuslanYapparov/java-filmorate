package ru.yandex.practicum.filmorate.util;

import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.restinteractionmodel.restcommand.UserRestCommand;
import ru.yandex.practicum.filmorate.model.restinteractionmodel.restview.UserRestView;

public class UserObjectConverter {

    public static UserRestView toRestView(User user) {
        return new UserRestView(user.getId(),
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                user.getFriends());
    }

    public static User toDomainObject(UserRestCommand userRestCommand) {
        User user = User.builder()
                .id(userRestCommand.getId())
                .email(userRestCommand.getEmail())
                .login(userRestCommand.getLogin())
                .name(userRestCommand.getName())
                .birthday(userRestCommand.getBirthday())
                .build();
        if (userRestCommand.getFriends() != null && !userRestCommand.getFriends().isEmpty()) {
            userRestCommand.getFriends().forEach(friendId -> user.getFriends().add(friendId));
        }
        return user;
    }

}