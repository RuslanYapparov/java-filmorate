package ru.yandex.practicum.filmorate.dao;

import java.util.List;

public interface FilmorateConstantStorageDao<E> {

    int getQuantity();

    E getById(long id);

    List<E> getAll();

}