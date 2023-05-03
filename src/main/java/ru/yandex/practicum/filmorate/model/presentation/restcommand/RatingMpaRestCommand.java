package ru.yandex.practicum.filmorate.model.presentation.restcommand;

import com.fasterxml.jackson.annotation.JsonProperty;

@lombok.AllArgsConstructor
@lombok.NoArgsConstructor
@lombok.Getter
public class RatingMpaRestCommand {
    @JsonProperty("id")
    int id;

}