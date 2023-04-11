package ru.yandex.practicum.filmorate.controller.storagecontrollers.impl;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import javax.validation.constraints.Positive;

import java.util.List;
import java.util.stream.Collectors;

import ru.yandex.practicum.filmorate.model.restinteractionmodel.restcommand.FilmRestCommand;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundInStorageException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.restinteractionmodel.restview.FilmRestView;
import ru.yandex.practicum.filmorate.storage.InMemoryStorage;
import ru.yandex.practicum.filmorate.util.FilmObjectConverter;

@Validated
@RestController
@RequestMapping("/films")
@lombok.extern.slf4j.Slf4j
@lombok.RequiredArgsConstructor
public class FilmStorageControllerImpl implements FilmStorageController {
    private final InMemoryStorage<Film> filmData;

    @Override
    @GetMapping
    public List<FilmRestView> getAll() {
        log.debug("Запрошен список всех фильмов. Количество сохраненных фильмов: {}", filmData.getSize());
        return filmData.getAll().stream()
                .map(FilmObjectConverter::toRestView)
                .collect(Collectors.toList());
    }

    @Override
    @GetMapping("{film_id}")
    public FilmRestView getOneById(@PathVariable(value = "film_id") @Positive long id)
            throws ObjectNotFoundInStorageException {
        Film film = filmData.getById(id);
        log.debug("Запрошен фильм с идентификатором {}. Фильм найден и отправлен клиенту", film.getId());
        return FilmObjectConverter.toRestView(film);
    }

    @Override
    @PostMapping
    public FilmRestView post(@RequestBody @Valid FilmRestCommand postFilmCommand) {
        Film film = FilmObjectConverter.toDomainObject(postFilmCommand);
        film = filmData.save(film);
        log.debug("Сохранен новый фильм '{}'. Присвоен идентификатор {}",
                film.getName(), film.getId());
        return FilmObjectConverter.toRestView(film);
    }

    @Override
    @PutMapping
    public FilmRestView put(@RequestBody @Valid FilmRestCommand putFilmCommand)
            throws ObjectNotFoundInStorageException {
        Film film = FilmObjectConverter.toDomainObject(putFilmCommand);
        film = filmData.update(film);
        log.debug("Обновлены данные фильма '{}'. Идентификатор фильма: {}", film.getName(), film.getId());
        return FilmObjectConverter.toRestView(film);
    }

    @Override
    @DeleteMapping
    public void deleteAll() {
        log.debug("Удалены данные всех фильмов из хранилища");
        filmData.deleteAll();
    }

    @Override
    @DeleteMapping("{film_id}")
    public FilmRestView deleteOneById(@PathVariable(value = "film_id") @Positive long id)
            throws ObjectNotFoundInStorageException {
        Film film = filmData.deleteById(id);
        log.debug("Запрошено удаление фильма с идентификатором {}. Фильм удален", id);
        return FilmObjectConverter.toRestView(film);
    }

}