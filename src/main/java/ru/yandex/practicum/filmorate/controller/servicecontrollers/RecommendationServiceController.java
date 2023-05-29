package ru.yandex.practicum.filmorate.controller.servicecontrollers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.presentation.restview.FilmRestView;
import ru.yandex.practicum.filmorate.model.service.Film;
import ru.yandex.practicum.filmorate.service.varimpl.FilmService;

import javax.validation.constraints.Positive;
import java.util.List;
import java.util.stream.Collectors;

@Validated
@RestController
@Slf4j
@RequiredArgsConstructor
public class RecommendationServiceController {
    @Qualifier("filmService")
    private final FilmService filmService;
    private final FilmMapper filmMapper;

    @GetMapping("/users/{user_id}/recommendations")
    public List<FilmRestView> getRecommendedFilmsForUser(@PathVariable(value = "user_id") @Positive long userId) {
        List<Film> recommendedFilms = filmService.getRecommendedFilmsForUser(userId);
        log.debug(String.format("Запрошен список рекоммендованных фильмов для пользователя id%d", userId));
        return this.mapListOfFilmsToListOfFilmRestViews(recommendedFilms);
    }

    private List<FilmRestView> mapListOfFilmsToListOfFilmRestViews(List<Film> films) {
        return films.stream()
                .map(filmMapper::toRestView)
                .collect(Collectors.toList());
    }

}