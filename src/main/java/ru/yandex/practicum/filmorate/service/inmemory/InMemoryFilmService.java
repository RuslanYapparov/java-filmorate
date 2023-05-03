package ru.yandex.practicum.filmorate.service.inmemory;

import java.util.List;

import ru.yandex.practicum.filmorate.model.presentation.restview.UserRestView;
import ru.yandex.practicum.filmorate.model.presentation.restview.FilmRestView;

public interface InMemoryFilmService {

    List<UserRestView> addLikeToFilmLikesSet(long filmId, long userId);

    List<UserRestView> removeLikeFromFilmLikesSet(long filmId, long userId);

    List<FilmRestView> getMostLikedFilms(int count);

}