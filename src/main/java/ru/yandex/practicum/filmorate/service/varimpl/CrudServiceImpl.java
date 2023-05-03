package ru.yandex.practicum.filmorate.service.varimpl;

import ru.yandex.practicum.filmorate.dao.FilmorateVariableStorageDao;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundInStorageException;
import ru.yandex.practicum.filmorate.service.CrudService;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CrudServiceImpl<T, TE, TRC> implements CrudService<T, TRC> {
    protected FilmorateVariableStorageDao<TE, T> objectDao;
    protected Function<TE, T> objectFromDbEntityMapper;
    protected Function<TRC, T> objectFromRestCommandMapper;

    public CrudServiceImpl(FilmorateVariableStorageDao<TE, T> objectDao) {
        this.objectDao = objectDao;
    }

    @Override
    public int getQuantity() {
        return this.objectDao.getQuantity();
    }

    @Override
    public void deleteAll() {
        this.objectDao.deleteAll();
    }

    @Override
    public T save(TRC objectRestCommand) {  // Учитывая, что на сохранение подается объект с пустыми полями-коллекциями,
        T object = objectFromRestCommandMapper.apply(objectRestCommand);        // Данный метод можно не переопределять
        return objectFromDbEntityMapper.apply(objectDao.save(object));
    }

    @Override
    public List<T> getAll() {        // Для всех объектов, имеющих поля-коллекции (User, Film), придется переопределить
        List<TE> allObjectEntities = this.objectDao.getAll();           // Методы getAll, getById, deleteById и update
        return allObjectEntities.stream()
                .map(objectFromDbEntityMapper)
                .collect(Collectors.toList());
    }

    @Override
    public T getById(long id) throws ObjectNotFoundInStorageException {
        TE objectEntity = this.objectDao.getById(id);
        return this.objectFromDbEntityMapper.apply(objectEntity);
    }

    @Override
    public T update(TRC objectRestCommand) throws ObjectNotFoundInStorageException {
        T object = objectFromRestCommandMapper.apply(objectRestCommand);
        return this.objectFromDbEntityMapper.apply(objectDao.update(object));
    }

    @Override
    public T deleteById(long objectId) throws ObjectNotFoundInStorageException {
        TE objectEntity = this.objectDao.deleteById(objectId, 0);
        return this.objectFromDbEntityMapper.apply(objectEntity);
    }

}