package ru.yandex.practicum.filmorate.model.presentation.restcommand;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class GenreRestCommand {
    @JsonProperty("id")
    int id;

}