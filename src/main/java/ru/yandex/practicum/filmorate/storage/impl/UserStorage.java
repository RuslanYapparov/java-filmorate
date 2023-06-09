package ru.yandex.practicum.filmorate.storage.impl;

import org.springframework.stereotype.Component;

import ru.yandex.practicum.filmorate.custom_validation.custom_validators.UserEmailAndNameValidator;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundInStorageException;
import ru.yandex.practicum.filmorate.exception.EmailValidationException;
import ru.yandex.practicum.filmorate.model.service.User;

import java.util.stream.Collectors;

@Component
public class UserStorage extends InMemoryStorageImpl<User> {

    @Override
    public User save(User userModel) throws EmailValidationException {
        userModel = UserEmailAndNameValidator.checkUserBeforeSaving(userModel,
                dataMap.values().stream()
                        .map(User::getEmail)
                        .collect(Collectors.toList()));
        long idForUser = produceId();
        userModel = userModel.toBuilder().id(idForUser).build();
        dataMap.put(idForUser, userModel);
        return userModel;
    }

    @Override
    public User update(User userModel) throws ObjectNotFoundInStorageException {
        userModel = UserEmailAndNameValidator.getUserWithCheckedName(userModel);
        if (dataMap.containsKey(userModel.getId())) {
            dataMap.put(userModel.getId(), userModel);
        } else {
            throw new ObjectNotFoundInStorageException("Данные не могут быть обновлены, т.к. пользователь " +
                    "с указанным идентификатором не был сохранен");
        }
        return userModel;
    }

}