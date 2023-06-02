package ru.yandex.practicum.filmorate.controller.storage_controllers.var_impl;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import javax.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;

import ru.yandex.practicum.filmorate.controller.storage_controllers.VariableStorageController;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.service.Film;
import ru.yandex.practicum.filmorate.model.presentation.rest_command.FilmRestCommand;
import ru.yandex.practicum.filmorate.model.presentation.rest_view.FilmRestView;
import ru.yandex.practicum.filmorate.service.var_impl.FilmService;

@Validated
@RestController
@RequestMapping("/films")
@Slf4j
@RequiredArgsConstructor
public class FilmStorageControllerImpl implements VariableStorageController<FilmRestCommand, FilmRestView> {
    private final FilmService filmService;
    private final FilmMapper filmMapper;

    @Override
    @GetMapping
    public List<FilmRestView> getAll() {
        log.debug("Запрошен список всех фильмов. Количество сохраненных фильмов: {}", filmService.getQuantity());
        return filmService.getAll().stream()
                .map(filmMapper::toRestView)
                .collect(Collectors.toList());
    }

    @Override
    @GetMapping("{film_id}")
    public FilmRestView getOneById(@PathVariable(value = "film_id") @Positive long filmId) {
        Film film = filmService.getById(filmId);
        log.debug("Запрошен фильм с идентификатором {}. Фильм найден и отправлен клиенту", film.getId());
        return filmMapper.toRestView(film);
    }

    @Override
    @PostMapping
    public FilmRestView post(@RequestBody @Valid FilmRestCommand postFilmCommand) {
        Film film = filmService.save(postFilmCommand);
        log.debug("Сохранен новый фильм '{}'. Присвоен идентификатор {}",
                film.getName(), film.getId());
        return filmMapper.toRestView(film);
    }

    @Override
    @PutMapping
    public FilmRestView put(@RequestBody @Valid FilmRestCommand putFilmCommand) {
        Film film = filmService.update(putFilmCommand);
        log.debug("Обновлены данные фильма '{}'. Идентификатор фильма: {}", film.getName(), film.getId());
        return filmMapper.toRestView(film);
    }

    @Override
    @DeleteMapping
    public void deleteAll() {
        log.debug("Удалены данные всех фильмов из хранилища");
        filmService.deleteAll();
    }

    @Override
    @DeleteMapping("{film_id}")
    public FilmRestView deleteOneById(@PathVariable(value = "film_id") @Positive long filmId) {
        Film film = filmService.deleteById(filmId);
        log.debug("Запрошено удаление фильма с идентификатором {}. Фильм удален", filmId);
        return filmMapper.toRestView(film);
    }

}