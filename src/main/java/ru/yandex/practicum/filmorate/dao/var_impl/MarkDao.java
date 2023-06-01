package ru.yandex.practicum.filmorate.dao.var_impl;

import java.util.List;

import ru.yandex.practicum.filmorate.dao.FilmorateVariableStorageDao;
import ru.yandex.practicum.filmorate.model.data.command.MarkCommand;

public interface MarkDao extends FilmorateVariableStorageDao<MarkCommand, MarkCommand> {

    List<Long> getAllUsersIdsWhoRatedFilm(long filmId);

    List<Long> getAllFilmIdsRatedByUser(long userId);

    List<Long> getAllIdsOfFilmsWithPositiveMarkByUserId(long userId);

}