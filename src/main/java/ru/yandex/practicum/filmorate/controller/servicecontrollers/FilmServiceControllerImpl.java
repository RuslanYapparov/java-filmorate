package ru.yandex.practicum.filmorate.controller.servicecontrollers;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.validation.constraints.Positive;

import java.util.List;

import ru.yandex.practicum.filmorate.model.controllercommandclasses.restcommand.impl.FilmRestCommand;
import ru.yandex.practicum.filmorate.model.controllercommandclasses.restcommand.impl.UserRestCommand;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundInStorageException;
import ru.yandex.practicum.filmorate.service.FilmService;

@Validated
@RestController
@lombok.extern.slf4j.Slf4j
@RequestMapping("/films")
@lombok.RequiredArgsConstructor
public class FilmServiceControllerImpl implements FilmServiceController {
    private final FilmService service;

    @Override
    @GetMapping("/popular")
    public List<FilmRestCommand> getPopularFilms(@RequestParam(name = "count", defaultValue = "10")
                                                 @Positive int size) {
        List<FilmRestCommand> popularFilms = service.getMostLikedFilms(size);
        log.debug(String.format("Запрошен список из %d наиболее популярных фильмов", size));
        return popularFilms;
    }

    @Override
    @DeleteMapping("{film_id}/like/{user_id}")
    public List<UserRestCommand> removeLike(@PathVariable(value = "film_id") @Positive long filmId,
                                            @PathVariable(value = "user_id") @Positive long userId)
            throws ObjectNotFoundInStorageException {
        List<UserRestCommand> filmLikesList = service.removeLikeFromFilmLikesSet(filmId, userId);
        log.debug(String.format("Пользователь id%d убрал лайк с фильма id%d", userId, filmId));
        return filmLikesList;
    }

    @Override
    @PutMapping("{film_id}/like/{user_id}")
    public List<UserRestCommand> addLike(@PathVariable(value = "film_id") @Positive long filmId,
                             @PathVariable(value = "user_id") @Positive long userId)
            throws ObjectNotFoundInStorageException {
        List<UserRestCommand> filmLikesList = service.addLikeToFilmLikesSet(filmId, userId);
        log.debug(String.format("Пользователь id%d поставил лайк фильму id%d", userId, filmId));
        return filmLikesList;
    }

}