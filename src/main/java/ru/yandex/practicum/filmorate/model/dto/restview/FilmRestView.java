package ru.yandex.practicum.filmorate.model.dto.restview;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.util.Set;

@lombok.EqualsAndHashCode
@lombok.AllArgsConstructor
@lombok.Builder   // Пока не нашел способ генерации FilmMapperImpl с правильным созданием объекта без данной аннотации
@lombok.NoArgsConstructor
@lombok.Getter
public class FilmRestView {
    @JsonProperty("id")
    private long id;
    @JsonProperty("name")
    private String name;
    @JsonProperty("description")
    private String description;
    @JsonProperty("releaseDate")
    private LocalDate releaseDate;
    @JsonProperty("duration")
    private int duration;
    @JsonProperty("likes")
    @lombok.Getter
    private Set<Long> likes;

}