package ru.yandex.practicum.filmorate.model.service;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;
import java.util.Set;

@Value
@Builder(toBuilder = true)
public class Film {
    long id;
    String name;
    String description;
    LocalDate releaseDate;
    int duration;
    byte rate;
    RatingMpa rating;
    Set<Long> likes;
    Set<Genre> genres;
    Set<Director> directors;

}