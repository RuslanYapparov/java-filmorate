package ru.yandex.practicum.filmorate.storage.impl;

import org.springframework.stereotype.Component;

import ru.yandex.practicum.filmorate.exception.ObjectNotFoundInStorageException;
import ru.yandex.practicum.filmorate.model.Film;

@Component
public class FilmStorage extends InMemoryStorageImpl<Film> {

    @Override
    public Film save(Film film) {
        long idForFilm = produceId();
        film = film.toBuilder().id(idForFilm).build();
        dataMap.put(idForFilm, film);
        return film;
    }

    @Override
    public Film update(Film film) throws ObjectNotFoundInStorageException {
        if (dataMap.containsKey(film.getId())) {
            dataMap.put(film.getId(), film);
        } else {
            throw new ObjectNotFoundInStorageException("Данные не могут быть обновлены, т.к. фильм с указанным " +
                    "идентификатором не был сохранен");
        }
        return film;
    }

}