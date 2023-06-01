package ru.yandex.practicum.filmorate.model.data;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;

@Value     // Мне кажется, экземпляры дата-классов по логике должны быть immutable-объектами, поэтому использую
@Builder(toBuilder = true)   // Данные аннотации в связке. Это вызывает дополнительные издержки в использовании
public class FilmEntity {   // Памяти и немного замедляет работу, но гарантирует, что поля не будут неожиданно изменены.
    long id;
    String name;
    String description;
    LocalDate releaseDate;
    int duration;
    float rate;
    int rating;

}