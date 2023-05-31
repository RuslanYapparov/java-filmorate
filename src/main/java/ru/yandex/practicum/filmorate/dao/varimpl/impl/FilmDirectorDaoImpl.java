package ru.yandex.practicum.filmorate.dao.varimpl.impl;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import java.util.List;

import ru.yandex.practicum.filmorate.dao.varimpl.FilmDirectorDao;
import ru.yandex.practicum.filmorate.dao.varimpl.FilmorateVariableStorageDaoImpl;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundInStorageException;
import ru.yandex.practicum.filmorate.model.data.DirectorEntity;
import ru.yandex.practicum.filmorate.model.data.command.FilmDirectorCommand;

@Repository
@Qualifier("filmGenreRepository")
public class FilmDirectorDaoImpl extends FilmorateVariableStorageDaoImpl<FilmDirectorCommand, FilmDirectorCommand>
        implements FilmDirectorDao {
    private final RowMapper<DirectorEntity> directorRowMapper;

    public FilmDirectorDaoImpl(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
        this.type = "film_director";
        this.objectEntityRowMapper = (resultSet, rowNumber) ->
                FilmDirectorCommand.builder()
                        .filmId(resultSet.getLong("film_id"))
                        .directorId(resultSet.getInt("director_id"))
                        .build();
        this.directorRowMapper = (resultSet, rowNumber) ->
                DirectorEntity.builder()
                        .id(resultSet.getInt("director_id"))
                        .name(resultSet.getString("director_name"))
                        .build();
    }

    @Override
    public int getQuantity() {
        sql = "select count(?_id) from ?s";
        return jdbcTemplate.queryForObject(sql, Integer.class, "film", type);
    }

    @Override
    public FilmDirectorCommand getById(long id) {
        return null;
    }

    @Override
    public List<FilmDirectorCommand> getAll() {
        sql = "select * from film_directors order by film_id";
        return jdbcTemplate.query(sql, objectEntityRowMapper);
    }

    @Override
    public FilmDirectorCommand deleteById(long filmId, long directorId) throws ObjectNotFoundInStorageException {
        sql = "delete from ?s where film_id = ? and director_id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, objectEntityRowMapper, type, filmId, directorId);
        } catch (DataRetrievalFailureException exception) {
            throw new ObjectNotFoundInStorageException(String.format("Фильм с id%d снят под руководством " +
                            "другого режиссера", filmId));
        }
    }

    @Override
    public FilmDirectorCommand save(FilmDirectorCommand filmDirectorCommand) {
        SqlRowSet filmDirectorRows;
        long filmId = filmDirectorCommand.getFilmId();
        int directorId = filmDirectorCommand.getDirectorId();
        sql = "select * from film_directors where film_id = ? and director_id = ?";
        filmDirectorRows = jdbcTemplate.queryForRowSet(sql, filmId, directorId);
        if (filmDirectorRows.next()) {
            return filmDirectorCommand;
        }
        sql = "insert into ?s (film_id, director_id) values (?, ?)";
        jdbcTemplate.update(sql, type, filmId, directorId);
        sql = "select * from ?s where film_id = ? and director_id =?";
        return jdbcTemplate.queryForObject(sql, objectEntityRowMapper, type, filmId, directorId);
    }

    @Override
    public List<Long> getAllFilmIdsByDirectorId(int directorId) {
        sql = "select * from directors where director_id = ?";
        try {
            jdbcTemplate.queryForObject(sql, directorRowMapper, directorId);
        } catch (DataRetrievalFailureException exception) {
            throw new ObjectNotFoundInStorageException(String.format("Режиссёр с id%d отсутствует в базе данных",
                    directorId));
        }
        sql = String.format("select film_id from %ss where director_id = %d", type, directorId);
        return jdbcTemplate.queryForList(sql, Long.class);
    }

    @Override
    public List<DirectorEntity> getAllDirectorEntitiesByFilmId(long filmId) {
        sql = String.format("select d.director_id, d.director_name from directors as d " +
                "right outer join film_directors as fd on d.director_id = fd.director_id where film_id = %d", filmId);
        return jdbcTemplate.query(sql, directorRowMapper);
    }

}