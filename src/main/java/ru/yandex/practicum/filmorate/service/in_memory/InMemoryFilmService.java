package ru.yandex.practicum.filmorate.service.in_memory;

import java.util.List;

import ru.yandex.practicum.filmorate.model.presentation.rest_view.UserRestView;
import ru.yandex.practicum.filmorate.model.presentation.rest_view.FilmRestView;

public interface InMemoryFilmService {

    List<UserRestView> addLikeToFilmLikesSet(long filmId, long userId);

    List<UserRestView> removeLikeFromFilmLikesSet(long filmId, long userId);

    List<FilmRestView> getMostLikedFilms(int count);

}