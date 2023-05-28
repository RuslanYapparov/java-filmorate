package ru.yandex.practicum.filmorate.model.presentation.restview;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class EventFeedRestView {
    @JsonProperty("eventId")
    long eventId;
    @JsonProperty("timestamp")
    long timestamp;
    @JsonProperty("userId")
    long userId;
    @JsonProperty("eventType")
    String eventType;
    @JsonProperty("operation")
    String operation;
    @JsonProperty("entityId")
    long entityId;
}
