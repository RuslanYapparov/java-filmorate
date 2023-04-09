package ru.yandex.practicum.filmorate.storage;

import java.util.List;

public interface InMemoryStorage<T> {

    long produceId();

    int getSize();

    T save(T object);

    T update(T object);

    T getById(long id);

    List<T> getAll();

    T deleteById(long id);

    void deleteAll();

}