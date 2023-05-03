package ru.yandex.practicum.filmorate.dao;

public interface FilmorateVariableStorageDao<E, T> extends FilmorateConstantStorageDao<E> {  // T - Type - тип объекта
                                                                                                    // Сервисного слоя
    E save(T object);

    E update(T object);

    E deleteById(long objectId, long anotherObjectId);

    void deleteAll();

}