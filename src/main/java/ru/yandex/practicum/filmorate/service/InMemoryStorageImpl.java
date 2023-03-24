package ru.yandex.practicum.filmorate.service;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.StorageManagementException;

@Service
public class InMemoryStorageImpl<T> implements InMemoryStorage<T> {
    private int idCounter;
    private final Map<Integer, T> dataMap = new HashMap<>();

    public Integer produceId() {
        return ++idCounter;
    }

    public Integer getSize() {
        return dataMap.size();
    }

    public T save(int id, T object) {
        dataMap.put(id, object);
        return object;
    }

    public T update(int id, T object) throws StorageManagementException {
        if (dataMap.containsKey(id)) {
            dataMap.put(id, object);
        } else {
            throw new StorageManagementException("Данные не могут быть обновлены, т.к. объект с указанным " +
                    "идентификатором не был сохранен");
        }
        return object;
    }

    public T getById(int id) throws StorageManagementException {
        if (dataMap.containsKey(id)) {
            return dataMap.get(id);
        } else {
            throw new StorageManagementException("Объект с указанным идентификатором не был сохранен");
        }
    }

    public List<T> getAll() {
        return new ArrayList<>(this.dataMap.values());
    }

    public T deleteById(int id) throws StorageManagementException {
        if (dataMap.containsKey(id)) {
            return dataMap.remove(id);
        } else {
            throw new StorageManagementException("Объект с указанным идентификатором не был сохранен");
        }
    }

    public void deleteAll() {
        this.dataMap.clear();
    }

}