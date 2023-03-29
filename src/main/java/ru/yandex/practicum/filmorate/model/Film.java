package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import ru.yandex.practicum.filmorate.customvalidation.customannotations.ReleaseDateAfterCinemaBirthday;

import javax.validation.constraints.*;
import java.time.LocalDate;

@lombok.Value     // Мне кажется, экземпляры дата-классов по логике должны быть immutable-объектами, поэтому использую
@lombok.Builder(toBuilder = true)   // Данные аннотации в связке. Это вызывает дополнительные издержки в использовании
public class Film {        // Памяти и немного замедляет работу, но гарантирует, что поля не будут неожиданно изменены.
    @PositiveOrZero
    int id;
    @NotNull
    @NotBlank
    String name;
    @NotNull
    @Size(max = 200)
    String description;
    @ReleaseDateAfterCinemaBirthday
    // Кастомная аннотация, проверяющая неравенство null и не принимающая дату релиза раньше 28 декабря 1895 г.
    @Past
    LocalDate releaseDate;
    @Positive
    int duration;

    @JsonCreator     // Специальный конструктор-creator для конвертирования в и из json с помощью библиотеки jackson
    private Film(@JsonProperty("id") int id, @JsonProperty("name") String name,
                 @JsonProperty("description") String description, @JsonProperty("releaseDate") LocalDate releaseDate,
                 @JsonProperty("duration") int duration) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
    }

}