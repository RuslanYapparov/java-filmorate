package ru.yandex.practicum.filmorate.storage.impl;

import org.springframework.stereotype.Component;

import ru.yandex.practicum.filmorate.exception.ObjectNotFoundInStorageException;
import ru.yandex.practicum.filmorate.model.service.Film;

@Component
public class FilmStorage extends InMemoryStorageImpl<Film> {

    @Override
    public Film save(Film filmModel) {
        long idForFilm = produceId();
        filmModel = filmModel.toBuilder().id(idForFilm).build();
        dataMap.put(idForFilm, filmModel);
        return filmModel;
    }

    @Override
    public Film update(Film filmModel) throws ObjectNotFoundInStorageException {
        if (dataMap.containsKey(filmModel.getId())) {
            dataMap.put(filmModel.getId(), filmModel);
        } else {
            throw new ObjectNotFoundInStorageException("Данные не могут быть обновлены, т.к. фильм с указанным " +
                    "идентификатором не был сохранен");
        }
        return filmModel;
    }

}