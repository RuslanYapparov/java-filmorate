package ru.yandex.practicum.filmorate.dao.varimpl;

import ru.yandex.practicum.filmorate.dao.FilmorateVariableStorageDao;
import ru.yandex.practicum.filmorate.model.data.FilmEntity;
import ru.yandex.practicum.filmorate.model.service.Film;

import java.util.List;

public interface FilmDao extends FilmorateVariableStorageDao<FilmEntity, Film> {

    List<FilmEntity> getCommonFilmsByRating(long userId, long friendId);
}
