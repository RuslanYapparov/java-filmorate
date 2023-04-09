package ru.yandex.practicum.filmorate.service;

import java.util.List;

import ru.yandex.practicum.filmorate.model.controllercommandclasses.restcommand.impl.FilmRestCommand;
import ru.yandex.practicum.filmorate.model.controllercommandclasses.restcommand.impl.UserRestCommand;

public interface FilmService {

    List<UserRestCommand> addLikeToFilmLikesSet(long filmId, long userId);

    List<UserRestCommand> removeLikeFromFilmLikesSet(long filmId, long userId);

    List<FilmRestCommand> getMostLikedFilms(int count);

}