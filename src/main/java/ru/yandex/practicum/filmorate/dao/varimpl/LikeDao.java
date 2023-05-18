package ru.yandex.practicum.filmorate.dao.varimpl;

import java.util.List;

import ru.yandex.practicum.filmorate.dao.FilmorateVariableStorageDao;
import ru.yandex.practicum.filmorate.model.data.LikeEntity;
import ru.yandex.practicum.filmorate.model.service.LikeCommand;

public interface LikeDao extends FilmorateVariableStorageDao<LikeEntity, LikeCommand> {

    List<Long> getMostLikedFilms(int count);

    List<Long> getAllUsersIdsWhoLikedFilm(long filmId);

    List<Long> getAllFilmIdsLikedByUser(long userId);

}