package ru.yandex.practicum.filmorate.controller.service_controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.mapper.RestViewListMapper;
import ru.yandex.practicum.filmorate.model.presentation.rest_view.FilmRestView;
import ru.yandex.practicum.filmorate.model.service.Film;
import ru.yandex.practicum.filmorate.service.var_impl.FilmService;

import javax.validation.constraints.Positive;
import java.util.List;

@Validated
@RestController
@Slf4j
@RequiredArgsConstructor
public class RecommendationServiceController {
    private final FilmService filmService;
    private final RestViewListMapper restViewListMapper;

    @GetMapping("/users/{user_id}/recommendations")
    public List<FilmRestView> getRecommendedFilmsForUser(@PathVariable(value = "user_id") @Positive long userId) {
        List<Film> recommendedFilms = filmService.getRecommendedFilmsForUser(userId);
        log.debug(String.format("Запрошен список рекоммендованных фильмов для пользователя id%d", userId));
        return restViewListMapper.mapListOfFilmsToListOfFilmRestViews(recommendedFilms);
    }

}