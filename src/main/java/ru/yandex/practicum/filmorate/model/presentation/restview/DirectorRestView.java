package ru.yandex.practicum.filmorate.model.presentation.restview;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class DirectorRestView {
    @JsonProperty("id")
    @Getter
    int id;
    @JsonProperty("name")
    String name;

}