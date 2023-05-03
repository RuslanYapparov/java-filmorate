package ru.yandex.practicum.filmorate.service;

import java.util.List;

import ru.yandex.practicum.filmorate.exception.ObjectNotFoundInStorageException;

public interface ReadConstantObjectService<T> {

    int getQuantity();

    T getById(long id) throws ObjectNotFoundInStorageException;

    List<T> getAll();

}