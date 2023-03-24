package ru.yandex.practicum.filmorate.controller;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import ru.yandex.practicum.filmorate.exception.StorageManagementException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.InMemoryStorage;
import ru.yandex.practicum.filmorate.service.InMemoryStorageImpl;

@RestController
@RequestMapping("/films")
@lombok.extern.slf4j.Slf4j
public class FilmController {
    // Комментарии c рассуждениями о работе контроллеров представлены в комментариях класса UserController
    private final InMemoryStorage<Film> filmData;

    @Autowired
    private FilmController() {
        this.filmData = new InMemoryStorageImpl<>();
    }

    @GetMapping
    public ResponseEntity<List<Film>> getAllFilms() {
        log.debug("Запрошен список всех сохраненных фильмов. " +
                "Количество сохраненных фильмов: {}", filmData.getSize());
        return ResponseEntity.ok(filmData.getAll());
    }

    @GetMapping("{filmId}")
    public ResponseEntity<Film> getFilmById(@PathVariable String filmId) {
        try {
            int id = Integer.parseInt(filmId.replace("id", ""));
            Film film = filmData.getById(id);
            log.debug("Запрошены данные о фильме с идентификатором {}. Данные найдены и отправлены клиенту", id);
            return ResponseEntity.ok(film);
        } catch (StorageManagementException | NumberFormatException exception) {
            log.debug("Запрошен данные о фильме с идентификатором {}. Фильм не найден", filmId);
            return ResponseEntity.notFound().build();
        }
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
    public ResponseEntity<Film> updateFilm(@Valid @RequestBody Film film) {
        try {
            filmData.update(film.getId(), film);
            log.debug("Обновлены данные фильма '{}'. Идентификатор фильма: {}", film.getName(), film.getId());
            return ResponseEntity.ok().body(film);
        } catch (StorageManagementException exception) {
            log.debug("Попытка обновления данных фильма не удалась. Причина: {}", exception.getMessage());
            return ResponseEntity.internalServerError().body(film);
        }
    }

    @DeleteMapping
    public ResponseEntity<Boolean> deleteAllFilms() {
        filmData.deleteAll();
        log.debug("Удалены данные всех фильмов из хранилища");
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("{filmId}")
    public ResponseEntity<Film> deleteFilmById(@PathVariable String filmId) {
        try {
            int id = Integer.parseInt(filmId.replace("id",""));
            Film film = filmData.deleteById(id);
            log.debug("Запрошено удаление фильма с идентификатором {}. Данные о фильме удалены", id);
            return ResponseEntity.ok(film);
        } catch (StorageManagementException | NumberFormatException exception) {
            log.debug("Запрошено удаление данных о фильме с идентификатором {}. Данные не найдены", filmId);
            return ResponseEntity.notFound().build();
        }
    }

}