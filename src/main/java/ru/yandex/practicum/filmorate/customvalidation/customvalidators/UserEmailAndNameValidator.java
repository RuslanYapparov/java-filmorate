package ru.yandex.practicum.filmorate.customvalidation.customvalidators;

import ru.yandex.practicum.filmorate.exception.UserValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.stream.Collectors;

public class UserEmailAndNameValidator {

    public static User checkUserBeforeSaving(User user, List<User> savedUsers) throws UserValidationException {
        String[] emailElements = user.getEmail().split("@");
        if (!emailElements[1].contains(".")) {
            throw new UserValidationException("Неправильный формат адреса электронной почты");
        }
        List<String> savedUserEmails = savedUsers.stream().map(User::getEmail).collect(Collectors.toList());
        if (savedUserEmails.contains(user.getEmail())) {
            throw new UserValidationException("Пользователь с таким адресом электронной почты уже был сохранен");
        }
        return getUserWithCheckedName(user);
    }

    public static User getUserWithCheckedName(User user) {
        if (user.getName() == null || user.getName().isBlank() || user.getName().isEmpty()) {
            user = user.toBuilder().name(user.getLogin()).build();
        }
        return user;
    }

}