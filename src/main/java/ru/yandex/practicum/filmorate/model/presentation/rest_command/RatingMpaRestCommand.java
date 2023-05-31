package ru.yandex.practicum.filmorate.model.presentation.rest_command;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class RatingMpaRestCommand {
    @JsonProperty("id")
    int id;

}