package ru.yandex.practicum.filmorate.service.var_impl.impl;

import org.springframework.stereotype.Service;

import ru.yandex.practicum.filmorate.dao.FilmorateVariableStorageDao;
import ru.yandex.practicum.filmorate.mapper.DirectorMapper;
import ru.yandex.practicum.filmorate.model.data.DirectorEntity;
import ru.yandex.practicum.filmorate.model.presentation.rest_command.DirectorRestCommand;
import ru.yandex.practicum.filmorate.model.service.Director;
import ru.yandex.practicum.filmorate.service.var_impl.CrudServiceImpl;

@Service
public class DirectorServiceImpl extends CrudServiceImpl<Director, DirectorEntity, DirectorRestCommand> {

    public DirectorServiceImpl(FilmorateVariableStorageDao<DirectorEntity, Director> objectDao,
                               DirectorMapper directorMapper) {
        super(objectDao);
        this.objectFromDbEntityMapper = directorMapper::fromDbEntity;
        this.objectFromRestCommandMapper = directorMapper::fromRestCommand;
    }

}