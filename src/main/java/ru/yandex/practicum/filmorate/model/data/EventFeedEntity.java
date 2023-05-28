package ru.yandex.practicum.filmorate.model.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Getter
@Builder(toBuilder = true)
public class EventFeedEntity {
    long eventId;
    long timestamp;
    long userId;
    String eventType;
    String operation;
    long entityId;

    public String getEventType() {
        return eventType;
    }
}
