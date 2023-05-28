package ru.yandex.practicum.filmorate.model.presentation.restcommand;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class EventFeedRestCommand {
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

    public String getEventType() {
        return eventType;
    }
}
