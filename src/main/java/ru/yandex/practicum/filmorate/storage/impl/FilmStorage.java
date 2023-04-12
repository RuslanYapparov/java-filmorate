package ru.yandex.practicum.filmorate.storage.impl;

import org.springframework.stereotype.Component;

import ru.yandex.practicum.filmorate.exception.ObjectNotFoundInStorageException;
import ru.yandex.practicum.filmorate.model.FilmModel;

@Component
public class FilmStorage extends InMemoryStorageImpl<FilmModel> {

    @Override
    public FilmModel save(FilmModel filmModel) {
        long idForFilm = produceId();
        filmModel = filmModel.toBuilder().id(idForFilm).build();
        dataMap.put(idForFilm, filmModel);
        return filmModel;
    }

    @Override
    public FilmModel update(FilmModel filmModel) throws ObjectNotFoundInStorageException {
        if (dataMap.containsKey(filmModel.getId())) {
            dataMap.put(filmModel.getId(), filmModel);
        } else {
            throw new ObjectNotFoundInStorageException("Данные не могут быть обновлены, т.к. фильм с указанным " +
                    "идентификатором не был сохранен");
        }
        return filmModel;
    }

}