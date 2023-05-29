package ru.yandex.practicum.filmorate.model.data;

import lombok.Builder;
import lombok.Value;

import java.time.Instant;

@Value
@Builder(toBuilder = true)
public class EventEntity {
    long id;
    Instant timestamp;
    long userId;
    String eventType;
    String operation;
    long entityId;

}