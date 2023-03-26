package ru.yandex.practicum.filmorate.service;

import java.util.List;
// Приложение позволяет хранить валидные объекты двух типов. Для хранения данных в RAM используется HashMap
// Данный интерфейс по сути переназывает методы интерфеса Map + добавляет функцию предоставления id для сохранения

public interface InMemoryStorage<T> {

    Integer produceId();

    Integer getSize();

    T save(int id, T object);

    T update(int id, T object);

    T getById(int id);

    List<T> getAll();

    T deleteById(int id);

    void deleteAll();

}
