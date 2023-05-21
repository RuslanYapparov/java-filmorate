package ru.yandex.practicum.filmorate.dao.varimpl;

import java.util.List;

import ru.yandex.practicum.filmorate.dao.FilmorateVariableStorageDao;
import ru.yandex.practicum.filmorate.model.data.command.FilmGenreCommand;
import ru.yandex.practicum.filmorate.model.service.Genre;

public interface FilmGenreDao extends FilmorateVariableStorageDao<FilmGenreCommand, FilmGenreCommand> {

    List<Long> getAllFilmIdsByGenre(Genre genre);

    List<Genre> getAllGenresOfFilmByFilmId(long filmId);

}