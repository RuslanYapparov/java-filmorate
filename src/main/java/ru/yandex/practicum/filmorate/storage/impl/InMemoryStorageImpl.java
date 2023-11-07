package ru.yandex.practicum.filmorate.storage.impl;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

import ru.yandex.practicum.filmorate.exception.ObjectNotFoundInStorageException;
import ru.yandex.practicum.filmorate.storage.InMemoryStorage;

public class InMemoryStorageImpl<T> implements InMemoryStorage<T> {
    protected long idCounter = 0;
    protected final Map<Long, T> dataMap = new HashMap<>();

    @Override
    public long produceId() {
        return ++idCounter;
    }

    @Override
    public int getSize() {
        return dataMap.size();
    }

    @Override
    public T save(T object) {
        dataMap.put(produceId(), object);
        return object;
    }

    @Override
    public T update(T object) throws ObjectNotFoundInStorageException {
        return object;
    }

    @Override
    public T getById(long objectId) throws ObjectNotFoundInStorageException {
        if (dataMap.containsKey(objectId)) {
            return dataMap.get(objectId);
        } else {
            throw new ObjectNotFoundInStorageException("Объект с указанным идентификатором не был сохранен");
        }
    }

    @Override
    public List<T> getAll() {
        return new ArrayList<>(dataMap.values());
    }

    @Override
    public T deleteById(long objectId) throws ObjectNotFoundInStorageException {
        if (dataMap.containsKey(objectId)) {
            return dataMap.remove(objectId);
        } else {
            throw new ObjectNotFoundInStorageException("Объект с указанным идентификатором не был сохранен");
        }
    }

    @Override
    public void deleteAll() {
        dataMap.clear();
    }

}