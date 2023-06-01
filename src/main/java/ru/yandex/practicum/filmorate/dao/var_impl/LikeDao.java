package ru.yandex.practicum.filmorate.dao.var_impl;

import java.util.List;

import ru.yandex.practicum.filmorate.dao.FilmorateVariableStorageDao;
import ru.yandex.practicum.filmorate.model.data.command.LikeCommand;

public interface LikeDao extends FilmorateVariableStorageDao<LikeCommand, LikeCommand> {

    List<Long> getAllUsersIdsWhoLikedFilm(long filmId);

    List<Long> getAllFilmIdsLikedByUser(long userId);

}