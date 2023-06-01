package ru.yandex.practicum.filmorate.dao.var_impl.impl;

import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

import ru.yandex.practicum.filmorate.dao.var_impl.FilmorateVariableStorageDaoImpl;
import ru.yandex.practicum.filmorate.dao.var_impl.MarkDao;
import ru.yandex.practicum.filmorate.exception.ObjectAlreadyExistsException;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundInStorageException;
import ru.yandex.practicum.filmorate.model.data.command.MarkCommand;

@Repository
public class MarkDaoImpl extends FilmorateVariableStorageDaoImpl<MarkCommand, MarkCommand>
        implements MarkDao {

    public MarkDaoImpl(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
        this.type = "user_mark";
        this.objectEntityRowMapper = (resultSet, rowNumber) ->
                MarkCommand.builder()
                        .userId(resultSet.getLong("user_id"))
                        .filmId(resultSet.getLong("film_id"))
                        .rating(resultSet.getInt("rating"))
                        .build();
    }

    @Override
    public int getQuantity() {
        sql = "select count(?_id) from ?s";
        return jdbcTemplate.queryForObject(sql, Integer.class, "user", type);
    }

    @Override
    public MarkCommand getById(long id) {
        return null;
    }

    @Override
    public List<MarkCommand> getAll() {
        sql = "select * from user_marks order by film_id";
        return jdbcTemplate.query(sql, objectEntityRowMapper);
    }

    @Override
    public MarkCommand deleteById(long filmId, long userId) throws ObjectNotFoundInStorageException {
        sql = "select * from user_marks where film_id = ? and user_id = ?";
        MarkCommand markCommand;
        try {
            markCommand = jdbcTemplate.queryForObject(sql, objectEntityRowMapper, filmId, userId);
        } catch (DataRetrievalFailureException exception) {
            throw new ObjectNotFoundInStorageException(String.format("Пользователь с id%d не ставил оценку " +
                            "фильму с id%d", userId, filmId));
        }
        sql = "delete from user_marks where film_id = ? and user_id = ?";
        jdbcTemplate.update(sql, filmId, userId);
        sql = "update films set rate = (select avg(rating) from user_marks where film_id = ?) where film_id = ?";
        jdbcTemplate.update(sql, filmId, filmId);
        return markCommand;
    }

    @Override
    public MarkCommand save(MarkCommand markCommand) throws ObjectAlreadyExistsException {
        SqlRowSet friendshipRows;
        long filmId = markCommand.getFilmId();
        long userId = markCommand.getUserId();
        int rating = markCommand.getRating();
        sql = "select * from user_marks where film_id = ? and user_id = ?";
        friendshipRows = jdbcTemplate.queryForRowSet(sql, filmId, userId);
        if (friendshipRows.next()) {
            return this.update(markCommand);
        }
        sql = "insert into user_marks (film_id, user_id, rating) values (?, ?, ?)";
        jdbcTemplate.update(sql, filmId, userId, rating);
        sql = "update films set rate = (select avg(rating) from user_marks where film_id = ?) where film_id = ?";
        jdbcTemplate.update(sql, filmId, filmId);
        sql = "select * from user_marks where film_id = ? and user_id = ?";
        return jdbcTemplate.queryForObject(sql, objectEntityRowMapper, filmId, userId);
    }

    @Override
    public MarkCommand update(MarkCommand markCommand) {
        long filmId = markCommand.getFilmId();
        long userId = markCommand.getUserId();
        int rating = markCommand.getRating();
        sql = "update user_marks set rating = ? where user_id = ? and fim_id = ?";
        jdbcTemplate.update(sql, rating, userId, filmId);
        sql = "update films set rate = (select avg(rating) from user_marks where film_id = ?) where film_id = ?";
        jdbcTemplate.update(sql, filmId, filmId);
        sql = "select * from user_marks where film_id = ? and user_id = ?";
        return jdbcTemplate.queryForObject(sql, objectEntityRowMapper, filmId, userId);
    }

    @Override
    public List<Long> getAllUsersIdsWhoRatedFilm(long filmId) throws ObjectNotFoundInStorageException {
        sql = "select * from user_marks where film_id = ? order by user_id";
        try {
            return jdbcTemplate.query(sql, objectEntityRowMapper, filmId).stream()
                    .map(MarkCommand::getUserId)
                    .collect(Collectors.toList());
        } catch (DataRetrievalFailureException exception) {
            throw new ObjectNotFoundInStorageException(String.format(
                    "У фильма с идентификатором %d пока отсутствуют оценкки...", filmId));
        }
    }

    @Override
    public List<Long> getAllFilmIdsRatedByUser(long userId) throws ObjectNotFoundInStorageException {
        sql = "select * from user_marks where user_id = ?";
        try {
            return jdbcTemplate.query(sql, objectEntityRowMapper, userId).stream()
                    .map(MarkCommand::getFilmId)
                    .collect(Collectors.toList());
        } catch (DataRetrievalFailureException exception) {
            throw new ObjectNotFoundInStorageException(String.format(
                    "Пользователь с идентификатором %d пока не оценивал фильмы...", userId));
        }
    }

    @Override
    public List<Long> getAllIdsOfFilmsWithPositiveMarkByUserId(long userId) throws ObjectNotFoundInStorageException {
        sql = "select * from user_marks where user_id = ? and rating > 5";
        try {
            return jdbcTemplate.query(sql, objectEntityRowMapper, userId).stream()
                    .map(MarkCommand::getFilmId)
                    .collect(Collectors.toList());
        } catch (DataRetrievalFailureException exception) {
            throw new ObjectNotFoundInStorageException(String.format(
                    "Пользователь с идентификатором %d пока не оценивал фильмы...", userId));
        }
    }

}