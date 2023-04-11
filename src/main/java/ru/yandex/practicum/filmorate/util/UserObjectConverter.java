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

    public static User toDomainObject(UserRestCommand restUser) {
        User user = User.builder()
                .id(restUser.getId())
                .email(restUser.getEmail())
                .login(restUser.getLogin())
                .name(restUser.getName())
                .birthday(restUser.getBirthday())
                .build();
        if (restUser.getFriends() != null && !restUser.getFriends().isEmpty()) {
            restUser.getFriends().forEach(id -> user.getFriends().add(id));
        }
        return user;
    }

}