package ru.yandex.practicum.filmorate.model.dto.restcommand;

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
    private long id;
    @JsonProperty("name")
    @NotNull
    @NotBlank
    private String name;
    @JsonProperty("description")
    @NotNull
    @Size(max = 200)
    private String description;
    @JsonProperty("releaseDate")
    @ReleaseDateAfterCinemaBirthday
    // Кастомная аннотация, проверяющая неравенство null и не принимающая дату релиза раньше 28 декабря 1895 г.
    @Past
    private LocalDate releaseDate;
    @JsonProperty("duration")
    @Positive
    private int duration;
    @JsonProperty("likes")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Set<Long> likes;

}