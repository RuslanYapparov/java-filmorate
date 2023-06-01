package ru.yandex.practicum.filmorate.dao.var_impl.impl;

import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.const_impl.FilmorateConstantStorageDaoImpl;
import ru.yandex.practicum.filmorate.dao.var_impl.EventDao;
import ru.yandex.practicum.filmorate.exception.InternalLogicException;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundInStorageException;
import ru.yandex.practicum.filmorate.model.data.EventEntity;
import ru.yandex.practicum.filmorate.model.service.EventOperation;
import ru.yandex.practicum.filmorate.model.service.EventType;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public class EventDaoImpl extends FilmorateConstantStorageDaoImpl<EventEntity> implements EventDao {
    public EventDaoImpl(JdbcTemplate template) {
        super(template);
        this.type = "event";
        this.objectEntityRowMapper = (resultSet, rowNumber) ->
                EventEntity.builder()
                        .id(resultSet.getLong("event_id"))
                        .timestamp(Instant.ofEpochMilli(resultSet.getLong("event_timestamp")))
                        .userId(resultSet.getLong("user_id"))
                        .eventType(resultSet.getString("event_type"))
                        .operation(resultSet.getString("operation"))
                        .entityId(resultSet.getLong("entity_id"))
                        .build();
    }

    @Override
    public EventEntity save(long userId, EventType type, EventOperation operation, long entityId) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        sql = "insert into events (event_timestamp, user_id, event_type, operation, entity_id) values (?, ?, ?, ?, ?)";
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, Instant.now().toEpochMilli());
            ps.setLong(2, userId);
            ps.setString(3, type.name());
            ps.setString(4, operation.name());
            ps.setLong(5, entityId);
            return ps;
        }, keyHolder);
        long eventId = Optional.ofNullable(keyHolder.getKey())
                .orElseThrow(() -> new InternalLogicException("Произошла непредвиденная ошбика сохранения последнего " +
                        "действия пользователя с id" + userId + " в ленте-истории. Пожалуйста, повторите попытку. " +
                        "Если ошибка повторится, пожалуйста, свяжитесь с разработчиками приложения"))
                .longValue();
        return this.getById(eventId);
    }

    @Override
    public List<EventEntity> getAllEventsByUserId(long userId) throws ObjectNotFoundInStorageException {
        sql = "select * from events where user_id = ? order by event_timestamp";

        try {
            return jdbcTemplate.query(sql, objectEntityRowMapper, userId);
        } catch (DataRetrievalFailureException exception) {
            throw new ObjectNotFoundInStorageException(String.format("Пользователь с идентификатором %d не оставлял "
                    + "событий в базе данных приложения", userId));
        }
    }

}
