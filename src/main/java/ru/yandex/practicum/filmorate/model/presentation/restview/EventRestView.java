package ru.yandex.practicum.filmorate.model.presentation.restview;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EventRestView {
    @JsonProperty("timestamp")
    long timestamp;
    @JsonProperty("userId")
    long userId;
    @JsonProperty("eventType")
    String eventType;
    @JsonProperty("operation")
    String operation;
    @JsonProperty("eventId")
    long id;
    @JsonProperty("entityId")
    long entityId;

}
