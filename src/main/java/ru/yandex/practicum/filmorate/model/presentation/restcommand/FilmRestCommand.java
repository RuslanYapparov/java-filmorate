package ru.yandex.practicum.filmorate.model.presentation.restcommand;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.constraints.*;

import lombok.*;

import java.time.LocalDate;
import java.util.Set;

import ru.yandex.practicum.filmorate.customvalidation.customannotations.ReleaseDateAfterCinemaBirthday;

@AllArgsConstructor
@NoArgsConstructor
@Getter
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
    private LocalDate releaseDate;
    @JsonProperty("duration")
    @Positive
    private int duration;
    @JsonProperty("rate")
    @PositiveOrZero
    private byte rate;
    @JsonProperty("mpa")
    private RatingMpaRestCommand mpa;
    @JsonProperty("likes")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Set<Long> likes;
    @JsonProperty("genres")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Set<GenreRestCommand> genres;
    @JsonProperty("directors")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Set<DirectorRestCommand> directors;

}