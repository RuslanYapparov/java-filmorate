package ru.yandex.practicum.filmorate.model.restinteractionmodel.restcommand;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.constraints.*;

import java.time.LocalDate;
import java.util.Set;

import ru.yandex.practicum.filmorate.customvalidation.customannotations.ReleaseDateAfterCinemaBirthday;

@lombok.Data
public class FilmRestCommand {
    @JsonProperty("id")
    @PositiveOrZero
    long id;
    @JsonProperty("name")
    @NotNull
    @NotBlank
    String name;
    @JsonProperty("description")
    @NotNull
    @Size(max = 200)
    String description;
    @JsonProperty("releaseDate")
    @ReleaseDateAfterCinemaBirthday
    // Кастомная аннотация, проверяющая неравенство null и не принимающая дату релиза раньше 28 декабря 1895 г.
    @Past
    LocalDate releaseDate;
    @JsonProperty("duration")
    @Positive
    int duration;
    @JsonProperty("likes")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    Set<Long> likes;

}