package ru.yandex.practicum.filmorate.dao.varimpl;

import ru.yandex.practicum.filmorate.dao.FilmorateVariableStorageDao;
import ru.yandex.practicum.filmorate.model.data.DirectorEntity;
import ru.yandex.practicum.filmorate.model.data.command.FilmDirectorCommand;

import java.util.List;

public interface FilmDirectorDao extends FilmorateVariableStorageDao<FilmDirectorCommand, FilmDirectorCommand> {

    List<Long> getAllFilmIdsByDirectorId(int directorId);

    List<DirectorEntity> getAllDirectorEntitiesByFilmId(long filmId);

}