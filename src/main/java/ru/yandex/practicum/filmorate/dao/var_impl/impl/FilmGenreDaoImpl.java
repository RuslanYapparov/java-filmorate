package ru.yandex.practicum.filmorate.dao.var_impl.impl;

import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

import ru.yandex.practicum.filmorate.dao.var_impl.FilmGenreDao;
import ru.yandex.practicum.filmorate.dao.var_impl.FilmorateVariableStorageDaoImpl;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundInStorageException;
import ru.yandex.practicum.filmorate.model.data.command.FilmGenreCommand;
import ru.yandex.practicum.filmorate.model.service.Genre;

@Repository
public class FilmGenreDaoImpl extends FilmorateVariableStorageDaoImpl<FilmGenreCommand, FilmGenreCommand>
        implements FilmGenreDao {

    public FilmGenreDaoImpl(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
        this.type = "film_genre";
        this.objectEntityRowMapper = (resultSet, rowNumber) ->
                FilmGenreCommand.builder()
                        .filmId(resultSet.getLong("film_id"))
                        .genreId(resultSet.getInt("genre_id"))
                        .build();
    }

    @Override
    public int getQuantity() {
        sql = "select count(?_id) from ?s";
        return jdbcTemplate.queryForObject(sql, Integer.class, "film", type);
    }

    @Override
    public FilmGenreCommand getById(long id) {
        return null;
    }

    @Override
    public List<FilmGenreCommand> getAll() {
        sql = "select * from film_genres order by film_id";
        return jdbcTemplate.query(sql, objectEntityRowMapper);
    }

    @Override
    public FilmGenreCommand deleteById(long filmId, long genreId) throws ObjectNotFoundInStorageException {
        sql = "delete from ?s where film_id = ? and genre_id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, objectEntityRowMapper, type, filmId, genreId);
        } catch (DataRetrievalFailureException exception) {
            throw new ObjectNotFoundInStorageException(String.format("Фильм с id%d не относится к жанру '%s'",
                    filmId, Genre.getGenreById((int) genreId).getByRus()));
        }
    }

    @Override
    public FilmGenreCommand save(FilmGenreCommand filmGenreCommand) {
        SqlRowSet filmGenreRows;
        long filmId = filmGenreCommand.getFilmId();
        int genreId = filmGenreCommand.getGenreId();
        sql = "select * from film_genres where film_id = ? and genre_id = ?";
        filmGenreRows = jdbcTemplate.queryForRowSet(sql, filmId, genreId);
        if (filmGenreRows.next()) {
            return filmGenreCommand;
        }
        sql = "insert into ?s (film_id, genre_id) values (?, ?)";
        jdbcTemplate.update(sql, type, filmId, genreId);
        sql = "select * from ?s where film_id = ? and genre_id =?";
        return jdbcTemplate.queryForObject(sql, objectEntityRowMapper, type, filmId, genreId);
    }

    @Override
    public List<Genre> getAllGenresOfFilmByFilmId(long filmId) throws ObjectNotFoundInStorageException {
        sql = "select * from film_genres where film_id = ? order by genre_id asc";
        try {
            return jdbcTemplate.query(sql, objectEntityRowMapper, filmId).stream()
                    .map(FilmGenreCommand::getGenreId)
                    .map(Genre::getGenreById)
                    .collect(Collectors.toList());
        } catch (DataRetrievalFailureException exception) {
            throw new ObjectNotFoundInStorageException(String.format(
                    "Для фильма с идентификатором %d пока не определены жанры...", filmId));
        }
    }

    @Override
    public List<Long> getAllFilmIdsByGenre(Genre genre) throws ObjectNotFoundInStorageException {
        sql = "select * from film_genres where genre_id = ?";
        int genreId = genre.getId();
        try {
            return jdbcTemplate.query(sql, objectEntityRowMapper, genreId).stream()
                    .map(FilmGenreCommand::getFilmId)
                    .collect(Collectors.toList());
        } catch (DataRetrievalFailureException exception) {
            throw new ObjectNotFoundInStorageException(String.format("Для жанра '%s' пока не определены фильмы...",
                    Genre.getGenreById(genreId).getByRus()));
        }
    }

}