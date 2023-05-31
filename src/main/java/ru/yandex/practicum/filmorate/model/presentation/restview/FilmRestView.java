package ru.yandex.practicum.filmorate.model.presentation.restview;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDate;
import java.util.Set;

@EqualsAndHashCode
@AllArgsConstructor
@Builder(toBuilder = true)            // Пока не нашел способ генерации FilmMapperImpl с правильным созданием объекта
@NoArgsConstructor                                                                            // Без данной аннотации
@Getter
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
    @JsonProperty("rate")
    private byte rate;
    @JsonProperty("mpa")
    private RatingMpaRestView mpa;
    @JsonProperty("likes")
    private Set<Long> likes;
    @JsonProperty("genres")
    private Set<GenreRestView> genres;
    @JsonProperty("directors")
    private Set<DirectorRestView> directors;

}