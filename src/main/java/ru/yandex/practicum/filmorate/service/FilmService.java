package ru.yandex.practicum.filmorate.service;

import java.util.List;

import ru.yandex.practicum.filmorate.model.restinteractionmodel.restview.FilmRestView;
import ru.yandex.practicum.filmorate.model.restinteractionmodel.restview.UserRestView;

public interface FilmService {

    List<UserRestView> addLikeToFilmLikesSet(long filmId, long userId);

    List<UserRestView> removeLikeFromFilmLikesSet(long filmId, long userId);

    List<FilmRestView> getMostLikedFilms(int count);

}