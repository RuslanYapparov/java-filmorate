package ru.yandex.practicum.filmorate.customvalidation.customvalidators;

import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.stream.Collectors;

public class UserValidator {

    public static void checkUsersEmailForDuplication(User user, List<User> savedUsers) throws ValidationException {
        List<String> savedUserEmails = savedUsers.stream().map(User::getEmail).collect(Collectors.toList());
        if (savedUserEmails.contains(user.getEmail())) {
            throw new ValidationException("Пользователь с таким адресом электронной почты уже был сохранен");
        }
    }

    public static User checkUserNameAndReturnValidUser(User user) {
        if (user.getName() == null || user.getName().isBlank() || user.getName().isEmpty()) {
            user = user.toBuilder().name(user.getLogin()).build();
        }
        return user;
    }

}