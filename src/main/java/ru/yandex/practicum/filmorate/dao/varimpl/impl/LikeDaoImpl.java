package ru.yandex.practicum.filmorate.dao.varimpl.impl;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

import ru.yandex.practicum.filmorate.dao.varimpl.FilmorateVariableStorageDaoImpl;
import ru.yandex.practicum.filmorate.dao.varimpl.LikeDao;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundInStorageException;
import ru.yandex.practicum.filmorate.model.data.command.LikeCommand;
import ru.yandex.practicum.filmorate.model.service.EventFeed;

@Repository
@Qualifier("likeRepository")
public class LikeDaoImpl extends FilmorateVariableStorageDaoImpl<LikeCommand, LikeCommand>
        implements LikeDao {
    private final EventFeedDaoImpl eventFeedDao;

    public LikeDaoImpl(JdbcTemplate jdbcTemplate, EventFeedDaoImpl eventFeedDao) {
        super(jdbcTemplate);
        this.type = "like";
        this.eventFeedDao = eventFeedDao;
        this.objectEntityRowMapper = (resultSet, rowNumber) ->
                new LikeCommand(resultSet.getLong("film_id"),
                        resultSet.getLong("user_id"));
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
    public LikeCommand deleteById(long filmId, long userId) {
        sql = "delete from likes where film_id = ? and user_id = ?";
        try {
            jdbcTemplate.update(sql, filmId, userId);
            EventFeed eventFeed = EventFeed.builder()
                    .timestamp(new Timestamp(System.currentTimeMillis()).getTime())
                    .userId(userId)
                    .eventType("LIKE")
                    .operation("REMOVE")
                    .entityId(filmId)
                    .build();
            eventFeedDao.save(eventFeed);
        } catch (DataRetrievalFailureException exception) {
            throw new ObjectNotFoundInStorageException(String.format("Пользователь с id%d не ставил лайк фильму с id%d",
                    userId, filmId));
        }
        return new LikeCommand(filmId, userId);
    }

    @Override
    public LikeCommand save(LikeCommand likeCommand) {
        sql = "insert into likes (film_id, user_id) values (?, ?)";
        long filmId = likeCommand.getFilmId();
        long userId = likeCommand.getUserId();
        jdbcTemplate.update(sql, filmId, userId);
        EventFeed eventFeed = EventFeed.builder()
                .timestamp(new Timestamp(System.currentTimeMillis()).getTime())
                .userId(userId)
                .eventType("LIKE")
                .operation("ADD")
                .entityId(filmId)
                .build();
        eventFeedDao.save(eventFeed);
        sql = "select * from likes where film_id = ? and user_id = ?";
        return jdbcTemplate.queryForObject(sql, objectEntityRowMapper, filmId, userId);
    }

    @Override
    public List<Long> getAllUsersIdsWhoLikedFilm(long filmId) {
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
    public List<Long> getAllFilmIdsLikedByUser(long userId) {
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