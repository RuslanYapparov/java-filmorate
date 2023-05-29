package ru.yandex.practicum.filmorate.model.service;

import lombok.Value;
import lombok.Builder;

import java.time.Instant;

@Value
@Builder(toBuilder = true)
public class Event {
    long id;
    Instant timestamp;
    long userId;
    EventType eventType;
    EventOperation operation;
    long entityId;

}