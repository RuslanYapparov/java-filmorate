package ru.yandex.practicum.filmorate.dao.varimpl;

import java.util.List;

import ru.yandex.practicum.filmorate.dao.FilmorateVariableStorageDao;
import ru.yandex.practicum.filmorate.model.data.FilmGenreEntity;
import ru.yandex.practicum.filmorate.model.domain.FilmGenreCommand;
import ru.yandex.practicum.filmorate.model.domain.Genre;

public interface FilmGenreDao extends FilmorateVariableStorageDao<FilmGenreEntity, FilmGenreCommand> {

    List<Long> getAllFilmIdsByGenre(Genre genre);

    List<Genre> getAllGenresOfFilmByFilmId(long filmId);

}