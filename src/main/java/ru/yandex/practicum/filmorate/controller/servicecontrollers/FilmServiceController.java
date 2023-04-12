package ru.yandex.practicum.filmorate.controller.servicecontrollers;

import javax.validation.constraints.Positive;
import java.util.List;

import ru.yandex.practicum.filmorate.model.dto.restview.FilmRestView;
import ru.yandex.practicum.filmorate.model.dto.restview.UserRestView;

public interface FilmServiceController {

    List<UserRestView> addLike(@Positive long filmId, @Positive long userId);

    List<UserRestView> removeLike(@Positive long filmId, @Positive long userId);

    List<FilmRestView> getPopularFilms(@Positive int count);

}