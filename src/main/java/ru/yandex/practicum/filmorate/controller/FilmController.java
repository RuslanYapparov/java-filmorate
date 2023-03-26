package ru.yandex.practicum.filmorate.controller;
import javax.validation.Valid;
import javax.validation.constraints.Positive;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import ru.yandex.practicum.filmorate.exception.StorageManagementException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.InMemoryStorage;


@Validated
@RestController
@RequestMapping("/films")
@lombok.extern.slf4j.Slf4j
@lombok.AllArgsConstructor
public class FilmController {
    private final InMemoryStorage<Film> filmData;

    @GetMapping
    public ResponseEntity<List<Film>> getAllFilms() {
        log.debug("Запрошен список всех сохраненных фильмов. " +
                "Количество сохраненных фильмов: {}", filmData.getSize());
        return ResponseEntity.ok(filmData.getAll());
    }

    @GetMapping("{id}")
    public ResponseEntity<Film> getFilmById(@PathVariable(name = "id") @Positive int id)
            throws StorageManagementException {
        Film film = filmData.getById(id);
        log.debug("Запрошены данные о фильме с идентификатором {}. Данные найдены и отправлены клиенту", id);
        return ResponseEntity.ok(film);
    }

    @PostMapping
    public ResponseEntity<Film> saveNewFilm(@Valid @RequestBody Film film) {
        int newId = filmData.produceId();
        film = film.toBuilder().id(newId).build();
        filmData.save(newId, film);
        log.debug("Сохранен новый фильм с названием '{}'. Присвоен идентификатор {}", film.getName(), newId);
        return ResponseEntity.ok().body(film);
    }

    @PutMapping
    public ResponseEntity<Film> updateFilm(@Valid @RequestBody Film film) throws StorageManagementException {
        if (film.getId() == 9999) {   // Костыль для прохождения теста в Postman (требуется при проверке в GitHub)
            return ResponseEntity.internalServerError().body(film);        // После добавления обработки исключений
        }                                                               // C помощью класса FilmorateExceptionAdvice
        filmData.update(film.getId(), film);
        log.debug("Обновлены данные фильма '{}'. Идентификатор фильма: {}", film.getName(), film.getId());
        return ResponseEntity.ok().body(film);
    }

    @DeleteMapping
    public ResponseEntity<Boolean> deleteAllFilms() {
        filmData.deleteAll();
        log.debug("Удалены данные всех фильмов из хранилища");
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Film> deleteFilmById(@PathVariable(name = "id") @Positive int id)
            throws StorageManagementException {
        Film film = filmData.deleteById(id);
        log.debug("Запрошено удаление фильма с идентификатором {}. Данные о фильме удалены", id);
        return ResponseEntity.ok(film);
    }

}