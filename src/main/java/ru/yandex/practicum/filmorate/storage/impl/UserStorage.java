package ru.yandex.practicum.filmorate.storage.impl;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.customvalidation.customvalidators.UserEmailAndNameValidator;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundInStorageException;
import ru.yandex.practicum.filmorate.exception.UserValidationException;
import ru.yandex.practicum.filmorate.model.User;

@Component
public class UserStorage extends InMemoryStorageImpl<User> {

    @Override
    public User save(User user) throws UserValidationException {
        user = UserEmailAndNameValidator.checkUserBeforeSaving(user, this.getAll());
        long idForUser = produceId();
        user = user.toBuilder().id(idForUser).build();
        dataMap.put(idForUser, user);
        return user;
    }

    @Override
    public User update(User user) throws ObjectNotFoundInStorageException {
        user = UserEmailAndNameValidator.getUserWithCheckedName(user);
        if (dataMap.containsKey(user.getId())) {
            dataMap.put(user.getId(), user);
        } else {
            throw new ObjectNotFoundInStorageException("Данные не могут быть обновлены, т.к. пользователь " +
                    "с указанным идентификатором не был сохранен");
        }
        return user;
    }

}