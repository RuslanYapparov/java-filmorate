package ru.yandex.practicum.filmorate.controller.storagecontrollers;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import javax.validation.constraints.Positive;

import java.util.List;
import java.util.stream.Collectors;

import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.dto.restcommand.FilmRestCommand;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundInStorageException;
import ru.yandex.practicum.filmorate.model.FilmModel;
import ru.yandex.practicum.filmorate.model.dto.restview.FilmRestView;
import ru.yandex.practicum.filmorate.storage.InMemoryStorage;

@Validated
@RestController
@RequestMapping("/films")
@lombok.extern.slf4j.Slf4j
@lombok.RequiredArgsConstructor
public class FilmStorageControllerImpl implements FilmStorageController {
    private final InMemoryStorage<FilmModel> filmData;
    private final FilmMapper filmMapper;

    @Override
    @GetMapping
    public List<FilmRestView> getAll() {
        log.debug("Запрошен список всех фильмов. Количество сохраненных фильмов: {}", filmData.getSize());
        return filmData.getAll().stream()
                .map(filmMapper::toRestView)
                .collect(Collectors.toList());
    }

    @Override
    @GetMapping("{film_id}")
    public FilmRestView getOneById(@PathVariable(value = "film_id") @Positive long filmId)
            throws ObjectNotFoundInStorageException {
        FilmModel filmModel = filmData.getById(filmId);
        log.debug("Запрошен фильм с идентификатором {}. Фильм найден и отправлен клиенту", filmModel.getId());
        return filmMapper.toRestView(filmModel);
    }

    @Override
    @PostMapping
    public FilmRestView post(@RequestBody @Valid FilmRestCommand postFilmCommand) {
        FilmModel filmModel = filmMapper.toModel(postFilmCommand);
        filmModel = filmData.save(filmModel);
        log.debug("Сохранен новый фильм '{}'. Присвоен идентификатор {}",
                filmModel.getName(), filmModel.getId());
        return filmMapper.toRestView(filmModel);
    }

    @Override
    @PutMapping
    public FilmRestView put(@RequestBody @Valid FilmRestCommand putFilmCommand)
            throws ObjectNotFoundInStorageException {
        FilmModel filmModel = filmMapper.toModel(putFilmCommand);
        filmModel = filmData.update(filmModel);
        log.debug("Обновлены данные фильма '{}'. Идентификатор фильма: {}", filmModel.getName(), filmModel.getId());
        return filmMapper.toRestView(filmModel);
    }

    @Override
    @DeleteMapping
    public void deleteAll() {
        log.debug("Удалены данные всех фильмов из хранилища");
        filmData.deleteAll();
    }

    @Override
    @DeleteMapping("{film_id}")
    public FilmRestView deleteOneById(@PathVariable(value = "film_id") @Positive long filmId)
            throws ObjectNotFoundInStorageException {
        FilmModel filmModel = filmData.deleteById(filmId);
        log.debug("Запрошено удаление фильма с идентификатором {}. Фильм удален", filmId);
        return filmMapper.toRestView(filmModel);
    }

}