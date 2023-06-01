package ru.yandex.practicum.filmorate.model.presentation.rest_view;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class MarkRestView {
    @JsonProperty("userId")
    @Getter
    long userId;
    @JsonProperty("filmId")
    long filmId;
    @JsonProperty("rating")
    int rating;

}