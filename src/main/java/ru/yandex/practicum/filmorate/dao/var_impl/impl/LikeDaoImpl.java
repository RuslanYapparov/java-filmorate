package ru.yandex.practicum.filmorate.dao.var_impl.impl;

import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

import ru.yandex.practicum.filmorate.dao.var_impl.FilmorateVariableStorageDaoImpl;
import ru.yandex.practicum.filmorate.dao.var_impl.LikeDao;
import ru.yandex.practicum.filmorate.exception.ObjectAlreadyExistsException;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundInStorageException;
import ru.yandex.practicum.filmorate.model.data.command.LikeCommand;

@Repository
public class LikeDaoImpl extends FilmorateVariableStorageDaoImpl<LikeCommand, LikeCommand>
        implements LikeDao {

    public LikeDaoImpl(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
        this.type = "like";
        this.objectEntityRowMapper = (resultSet, rowNumber) ->
                LikeCommand.builder()
                        .filmId(resultSet.getLong("film_id"))
                        .userId(resultSet.getLong("user_id"))
                        .build();
    }

    @Override
    public int getQuantity() {
        sql = "select count(?_id) from ?s";
        return jdbcTemplate.queryForObject(sql, Integer.class, "film", type);
    }

    @Override
    public LikeCommand getById(long id) {
        return null;
    }

    @Override
    public List<LikeCommand> getAll() {
        sql = "select * from likes order by film_id";
        return jdbcTemplate.query(sql, objectEntityRowMapper);
    }

    @Override
    public LikeCommand deleteById(long filmId, long userId) throws ObjectNotFoundInStorageException {
        sql = "delete from likes where film_id = ? and user_id = ?";
        try {
            jdbcTemplate.update(sql, filmId, userId);
        } catch (DataRetrievalFailureException exception) {
            throw new ObjectNotFoundInStorageException(String.format("Пользователь с id%d не ставил лайк фильму с id%d",
                    userId, filmId));
        }
        return LikeCommand.builder().filmId(filmId).userId(userId).build();
    }

    @Override
    public LikeCommand save(LikeCommand likeCommand) throws ObjectAlreadyExistsException {
        SqlRowSet friendshipRows;
        long filmId = likeCommand.getFilmId();
        long userId = likeCommand.getUserId();
        sql = "select * from likes where film_id = ? and user_id = ?";
        friendshipRows = jdbcTemplate.queryForRowSet(sql, filmId, userId);
        if (friendshipRows.next()) {
            return likeCommand;
        }
        sql = "insert into likes (film_id, user_id) values (?, ?)";
        jdbcTemplate.update(sql, filmId, userId);
        sql = "select * from likes where film_id = ? and user_id = ?";
        return jdbcTemplate.queryForObject(sql, objectEntityRowMapper, filmId, userId);
    }

    @Override
    public List<Long> getAllUsersIdsWhoLikedFilm(long filmId) throws ObjectNotFoundInStorageException {
        sql = "select * from likes where film_id = ? order by user_id";
        try {
            return jdbcTemplate.query(sql, objectEntityRowMapper, filmId).stream()
                    .map(LikeCommand::getUserId)
                    .collect(Collectors.toList());
        } catch (DataRetrievalFailureException exception) {
            throw new ObjectNotFoundInStorageException(String.format(
                    "У фильма с идентификатором %d пока отсутствуют лайки...", filmId));
        }
    }

    @Override
    public List<Long> getAllFilmIdsLikedByUser(long userId) throws ObjectNotFoundInStorageException {
        sql = "select * from likes where user_id = ?";
        try {
            return jdbcTemplate.query(sql, objectEntityRowMapper, userId).stream()
                    .map(LikeCommand::getFilmId)
                    .collect(Collectors.toList());
        } catch (DataRetrievalFailureException exception) {
            throw new ObjectNotFoundInStorageException(String.format(
                    "Пользователь с идентификатором %d пока не ставил лайки...", userId));
        }
    }

}