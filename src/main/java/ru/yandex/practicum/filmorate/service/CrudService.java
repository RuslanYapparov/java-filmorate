package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.exception.ObjectNotFoundInStorageException;

public interface CrudService<T, C> extends ReadConstantObjectService<T> {
                             // C - RestCommand - тип объекта, поступающего в RequestBody HTTP-запроса
    T save(C objectRestCommand);

    T update(C objectRestCommand) throws ObjectNotFoundInStorageException;

    T deleteById(long objectId) throws ObjectNotFoundInStorageException;

    void deleteAll();

}