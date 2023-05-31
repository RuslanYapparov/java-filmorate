package ru.yandex.practicum.filmorate.service.var_impl;

import ru.yandex.practicum.filmorate.dao.FilmorateVariableStorageDao;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundInStorageException;
import ru.yandex.practicum.filmorate.service.CrudService;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CrudServiceImpl<T, E, C> implements CrudService<T, C> {
    protected FilmorateVariableStorageDao<E, T> objectDao;
    protected Function<E, T> objectFromDbEntityMapper;
    protected Function<C, T> objectFromRestCommandMapper;

    public CrudServiceImpl(FilmorateVariableStorageDao<E, T> objectDao) {
        this.objectDao = objectDao;
    }

    @Override
    public int getQuantity() {
        return objectDao.getQuantity();
    }

    @Override
    public void deleteAll() {
        objectDao.deleteAll();
    }

    @Override
    public T save(C objectRestCommand) {  // Учитывая, что на сохранение подается объект с пустыми полями-коллекциями,
        T object = objectFromRestCommandMapper.apply(objectRestCommand);        // Данный метод можно не переопределять
        return objectFromDbEntityMapper.apply(objectDao.save(object));
    }

    @Override
    public List<T> getAll() {        // Для всех объектов, имеющих поля-коллекции (User, Film), придется переопределить
        List<E> allObjectEntities = objectDao.getAll();           // Методы getAll, getById, deleteById и update
        return allObjectEntities.stream()
                .map(objectFromDbEntityMapper)
                .collect(Collectors.toList());
    }

    @Override
    public T getById(long id) throws ObjectNotFoundInStorageException {
        E objectEntity = objectDao.getById(id);
        return objectFromDbEntityMapper.apply(objectEntity);
    }

    @Override
    public T update(C objectRestCommand) throws ObjectNotFoundInStorageException {
        T object = objectFromRestCommandMapper.apply(objectRestCommand);
        return objectFromDbEntityMapper.apply(objectDao.update(object));
    }

    @Override
    public T deleteById(long objectId) throws ObjectNotFoundInStorageException {
        E objectEntity = objectDao.deleteById(objectId, 0);
        return objectFromDbEntityMapper.apply(objectEntity);
    }

}