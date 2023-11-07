package ru.yandex.practicum.filmorate.model.data;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;

@Value
@Builder(toBuilder = true)
public class FilmEntity {
    long id;
    String name;
    String description;
    LocalDate releaseDate;
    int duration;
    byte rate;
    int rating;

}