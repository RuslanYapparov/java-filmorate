package ru.yandex.practicum.filmorate.model.presentation.rest_command;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class DirectorRestCommand {
    @JsonProperty("id")
    int id;
    @JsonProperty("name")
    @NotBlank
    @Size(max = 255)
    String name;

}