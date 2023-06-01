package ru.yandex.practicum.filmorate.custom_validation.custom_validators;

import org.springframework.stereotype.Component;

import java.util.List;

import ru.yandex.practicum.filmorate.exception.ObjectAlreadyExistsException;
import ru.yandex.practicum.filmorate.exception.EmailValidationException;
import ru.yandex.practicum.filmorate.model.service.User;

@Component
public class UserEmailAndNameValidator {

    public static User checkUserBeforeSaving(User user, List<String> savedUserEmails)
            throws EmailValidationException {
        if (savedUserEmails.contains(user.getEmail())) {
            throw new ObjectAlreadyExistsException("Пользователь с таким адресом электронной почты уже был сохранен");
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

    private static void checkEmailForDotAfterAt(User user) throws EmailValidationException {
        String[] emailElements = user.getEmail().split("@");
        if (!emailElements[1].contains(".")) {
            throw new EmailValidationException("Неправильный формат адреса электронной почты");
        }
    }

}