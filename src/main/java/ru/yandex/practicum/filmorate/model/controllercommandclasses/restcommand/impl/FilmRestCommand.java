package ru.yandex.practicum.filmorate.model.controllercommandclasses.restcommand.impl;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import javax.validation.Valid;
import javax.validation.constraints.*;

import java.time.LocalDate;
import java.util.Set;

import ru.yandex.practicum.filmorate.customvalidation.customannotations.ReleaseDateAfterCinemaBirthday;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.controllercommandclasses.restcommand.ObjectRestCommand;

@lombok.ToString
@lombok.EqualsAndHashCode
@lombok.NoArgsConstructor
public class FilmRestCommand implements ObjectRestCommand<Film> {
    @JsonProperty("id")
    @PositiveOrZero
    @Getter
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

    public FilmRestCommand(Film film) {
        this.id = film.getId();
        this.name = film.getName();
        this.description = film.getDescription();
        this.releaseDate = film.getReleaseDate();
        this.duration = film.getDuration();
        this.likes = film.getLikes();
    }

    @Override
    public @Valid Film convertToDomainObject() {
        Film film = Film.builder()
                .id(this.id)
                .name(this.name)
                .description(this.description)
                .releaseDate(this.releaseDate)
                .duration(this.duration)
                .build();
        if (this.likes != null && !likes.isEmpty()) {
            this.likes.forEach(id -> film.getLikes().add(id));
        }
        return film;
    }

}