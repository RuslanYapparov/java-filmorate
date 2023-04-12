package ru.yandex.practicum.filmorate.customvalidation.customvalidators;

import org.springframework.stereotype.Component;

import javax.validation.Validation;
import javax.validation.Validator;
import java.util.List;
import java.util.stream.Collectors;

import ru.yandex.practicum.filmorate.exception.UserValidationException;
import ru.yandex.practicum.filmorate.model.UserModel;

@Component
public class UserEmailAndNameValidator {
    private static final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    public static UserModel checkUserBeforeSaving(UserModel userModel, List<UserModel> savedUserModels) throws UserValidationException {
        String[] emailElements = userModel.getEmail().split("@");
        if (!emailElements[1].contains(".")) {
            throw new UserValidationException("Неправильный формат адреса электронной почты");
        }
        List<String> savedUserEmails = savedUserModels.stream().map(UserModel::getEmail).collect(Collectors.toList());
        if (savedUserEmails.contains(userModel.getEmail())) {
            throw new UserValidationException("Пользователь с таким адресом электронной почты уже был сохранен");
        }
        return getUserWithCheckedName(userModel);
    }

    public static UserModel getUserWithCheckedName(UserModel userModel) {
        if (userModel.getName() == null || userModel.getName().isBlank() || userModel.getName().isEmpty()) {
            userModel = userModel.toBuilder().name(userModel.getLogin()).build();
        }
        return userModel;
    }

}