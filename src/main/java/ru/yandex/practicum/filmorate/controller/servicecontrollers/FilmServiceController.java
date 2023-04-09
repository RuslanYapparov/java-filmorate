package ru.yandex.practicum.filmorate.controller.servicecontrollers;

import javax.validation.constraints.Positive;
import java.util.List;

import ru.yandex.practicum.filmorate.model.controllercommandclasses.restcommand.impl.FilmRestCommand;
import ru.yandex.practicum.filmorate.model.controllercommandclasses.restcommand.impl.UserRestCommand;

public interface FilmServiceController {

    List<UserRestCommand> addLike(@Positive long filmId, @Positive long userId);

    List<UserRestCommand> removeLike(@Positive long filmId, @Positive long userId);

    List<FilmRestCommand> getPopularFilms(@Positive int count);

}