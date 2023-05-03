package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.exception.ObjectNotFoundInStorageException;

public interface CrudService<T, TRC> extends ReadConstantObjectService<T> {
                             // TRC - TypeRestCommand - тип объекта, поступающего в RequestBody HTTP-запроса
    T save(TRC objectRestCommand);

    T update(TRC objectRestCommand) throws ObjectNotFoundInStorageException;

    T deleteById(long objectId) throws ObjectNotFoundInStorageException;

    void deleteAll();

}