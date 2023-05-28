package ru.yandex.practicum.filmorate.dao.varimpl.impl;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.varimpl.FilmorateVariableStorageDaoImpl;
import ru.yandex.practicum.filmorate.model.data.EventFeedEntity;
import ru.yandex.practicum.filmorate.model.service.EventFeed;

import java.sql.PreparedStatement;
import java.sql.Statement;

@Repository
@Primary
@Qualifier("eventFeedRepository")
public class EventFeedDaoImpl extends FilmorateVariableStorageDaoImpl<EventFeedEntity, EventFeed> {

    public EventFeedDaoImpl(JdbcTemplate template) {
        super(template);
        this.type = "event";
        this.objectEntityRowMapper = (resultSet, rowNumber) ->
                EventFeedEntity.builder()
                        .eventId(resultSet.getLong("event_id"))
                        .timestamp(resultSet.getLong("timestamp_event"))
                        .userId(resultSet.getLong("user_id"))
                        .eventType(resultSet.getString("event_type"))
                        .operation(resultSet.getString("operation"))
                        .entityId(resultSet.getLong("entity_id"))
                        .build();
    }

    @Override
    public EventFeedEntity save(EventFeed eventFeed) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        long timestamp = eventFeed.getTimestamp();
        long userId = eventFeed.getUserId();
        String eventType = eventFeed.getEventType();
        String operation = eventFeed.getOperation();
        long entityId = eventFeed.getEntityId();
        sql = "insert into events (timestamp_event, user_id, event_type, operation, entity_id) values (?, ?, ?, ?, ?)";
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, timestamp);
            ps.setLong(2, userId);
            ps.setObject(3, eventType);
            ps.setObject(4, operation);
            ps.setLong(5, entityId);
            return ps;
        }, keyHolder);
        return this.getById(keyHolder.getKey().longValue());
    }
}
