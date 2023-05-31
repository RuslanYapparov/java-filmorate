package ru.yandex.practicum.filmorate.model.presentation.restview;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class RatingMpaRestView {
    @JsonProperty("id")
    @Getter
    int id;
    @JsonProperty("name")
    String name;

}