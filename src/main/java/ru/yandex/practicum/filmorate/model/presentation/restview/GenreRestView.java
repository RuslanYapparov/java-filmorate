package ru.yandex.practicum.filmorate.model.presentation.restview;

import com.fasterxml.jackson.annotation.JsonProperty;

@lombok.AllArgsConstructor
@lombok.NoArgsConstructor
public class GenreRestView {
    @JsonProperty("id")
    @lombok.Getter
    int id;
    @JsonProperty("name")
    String name;

}