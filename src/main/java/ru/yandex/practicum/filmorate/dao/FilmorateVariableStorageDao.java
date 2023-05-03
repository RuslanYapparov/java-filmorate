package ru.yandex.practicum.filmorate.dao;

public interface FilmorateVariableStorageDao<TE, T> extends FilmorateConstantStorageDao<TE> {  // T - Type - тип объекта
                                                                                                    // Сервисного слоя
    TE save(T object);

    TE update(T object);

    TE deleteById(long objectId, long anotherObjectId);

    void deleteAll();

}