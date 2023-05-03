package ru.yandex.practicum.filmorate.model.domain;

import java.time.LocalDate;
import java.util.Set;

@lombok.Value     // Мне кажется, экземпляры дата-классов по логике должны быть immutable-объектами, поэтому использую
@lombok.Builder(toBuilder = true)   // Данные аннотации в связке. Это вызывает дополнительные издержки в использовании
public class Film {   // Памяти и немного замедляет работу, но гарантирует, что поля не будут неожиданно изменены.
    long id;
    String name;
    String description;
    LocalDate releaseDate;
    int duration;
    byte rate;
    RatingMpa rating;
    Set<Long> likes;
    Set<Genre> genres;

}