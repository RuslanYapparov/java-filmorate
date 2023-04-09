package ru.yandex.practicum.filmorate.controller.storagecontrollers.impl;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import javax.validation.constraints.Positive;

import java.util.List;
import java.util.stream.Collectors;

import ru.yandex.practicum.filmorate.model.controllercommandclasses.restcommand.impl.FilmRestCommand;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundInStorageException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.InMemoryStorage;

@Validated
@RestController
@RequestMapping("/films")
@lombok.extern.slf4j.Slf4j
@lombok.RequiredArgsConstructor
public class FilmStorageControllerImpl implements FilmStorageController {
    private final InMemoryStorage<Film> filmData;

    @Override
    @GetMapping
    public List<FilmRestCommand> getAll() {
        log.debug("Запрошен список всех фильмов. Количество сохраненных фильмов: {}", filmData.getSize());
        return filmData.getAll().stream()
                .map(FilmRestCommand::new)
                .collect(Collectors.toList());
    }

    @Override
    @GetMapping("{film_id}")
    public FilmRestCommand getOneById(@PathVariable(value = "film_id") @Positive long id)
            throws ObjectNotFoundInStorageException {
        Film film = filmData.getById(id);
        log.debug("Запрошен фильм с идентификатором {}. Фильм найден и отправлен клиенту", film.getId());
        return new FilmRestCommand(film);
    }

    @Override
    @PostMapping
    public FilmRestCommand post(@RequestBody @Valid FilmRestCommand postFilmCommand) {
        Film film = postFilmCommand.convertToDomainObject();
        film = filmData.save(film);
        log.debug("Сохранен новый фильм '{}'. Присвоен идентификатор {}",
                film.getName(), film.getId());
        return new FilmRestCommand(film);
    }

    @Override
    @PutMapping
    public FilmRestCommand put(@RequestBody @Valid FilmRestCommand putFilmCommand)
            throws ObjectNotFoundInStorageException {
        Film film = putFilmCommand.convertToDomainObject();
        film = filmData.update(film);
        log.debug("Обновлены данные фильма '{}'. Идентификатор фильма: {}", film.getName(), film.getId());
        return new FilmRestCommand(film);
    }

    @Override
    @DeleteMapping
    public void deleteAll() {
        log.debug("Удалены данные всех фильмов из хранилища");
        filmData.deleteAll();
    }

    @Override
    @DeleteMapping("{film_id}")
    public FilmRestCommand deleteOneById(@PathVariable(value = "film_id") @Positive long id)
            throws ObjectNotFoundInStorageException {
        Film film = filmData.deleteById(id);
        log.debug("Запрошено удаление фильма с идентификатором {}. Фильм удален", id);
        return new FilmRestCommand(film);
    }

}