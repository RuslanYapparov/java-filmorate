package ru.yandex.practicum.filmorate.storage.impl;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.customvalidation.customvalidators.UserEmailAndNameValidator;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundInStorageException;
import ru.yandex.practicum.filmorate.exception.UserValidationException;
import ru.yandex.practicum.filmorate.model.UserModel;

@Component
public class UserStorage extends InMemoryStorageImpl<UserModel> {

    @Override
    public UserModel save(UserModel userModel) throws UserValidationException {
        userModel = UserEmailAndNameValidator.checkUserBeforeSaving(userModel, this.getAll());
        long idForUser = produceId();
        userModel = userModel.toBuilder().id(idForUser).build();
        dataMap.put(idForUser, userModel);
        return userModel;
    }

    @Override
    public UserModel update(UserModel userModel) throws ObjectNotFoundInStorageException {
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