package ru.yandex.practicum.filmorate.customvalidation.customvalidators;

import org.springframework.stereotype.Component;

import java.util.List;

import ru.yandex.practicum.filmorate.exception.UserValidationException;
import ru.yandex.practicum.filmorate.model.domain.User;

@Component
public class UserEmailAndNameValidator {

    public static User checkUserBeforeSaving(User user, List<String> savedUserEmails)
            throws UserValidationException {
        if (savedUserEmails.contains(user.getEmail())) {
            throw new UserValidationException("Пользователь с таким адресом электронной почты уже был сохранен");
        }
        return getUserWithCheckedName(user);
    }

    public static User getUserWithCheckedName(User user) {
        checkEmailForDotAfterAt(user);
        if (user.getName() == null || user.getName().isBlank() || user.getName().isEmpty()) {
            user = user.toBuilder().name(user.getLogin()).build();
        }
        return user;
    }

    private static void checkEmailForDotAfterAt(User user) throws UserValidationException {
        String[] emailElements = user.getEmail().split("@");
        if (!emailElements[1].contains(".")) {
            throw new UserValidationException("Неправильный формат адреса электронной почты");
        }
    }

}