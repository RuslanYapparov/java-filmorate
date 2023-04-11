package ru.yandex.practicum.filmorate.model.restinteractionmodel.restview;

import java.time.LocalDate;
import java.util.Set;

@lombok.Data
@lombok.AllArgsConstructor
@lombok.NoArgsConstructor
public class FilmRestView {
    long id;
    String name;
    String description;
    LocalDate releaseDate;
    int duration;
    @lombok.Getter
    Set<Long> likes;

}