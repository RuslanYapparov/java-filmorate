package ru.yandex.practicum.filmorate.model.service;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;
import java.util.Set;

@Value     // Мне кажется, экземпляры дата-классов по логике должны быть immutable-объектами, поэтому использую
@Builder(toBuilder = true)   // Данные аннотации в связке. Это вызывает дополнительные издержки в использовании
public class Film {   // Памяти и немного замедляет работу, но гарантирует, что поля не будут неожиданно изменены.
    long id;
    String name;
    String description;
    LocalDate releaseDate;
    int duration;
    float rate;
    RatingMpa rating;
    Set<Long> marksFrom;
    Set<Genre> genres;
    Set<Director> directors;

}