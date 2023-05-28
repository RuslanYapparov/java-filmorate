package ru.yandex.practicum.filmorate.model.service;

import lombok.*;

import java.sql.Timestamp;


@Data
@Builder(toBuilder = true)
public class EventFeed {
    private final long eventId;
    private final long timestamp;
    private final long userId;
    private final String eventType;
    private final String operation;
    private final long entityId;

    public String getEventType() {
        return eventType;
    }
}
