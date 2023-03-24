package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import ru.yandex.practicum.filmorate.customvalidation.customannotations.NotImmortal;
import ru.yandex.practicum.filmorate.customvalidation.customannotations.WithoutSpaces;

import javax.validation.constraints.*;
import java.time.LocalDate;

@lombok.Value     // Мне кажется, экземпляры дата-классов по логике должны быть immutable-объектами, поэтому использую
@lombok.Builder(toBuilder = true)   // Данные аннотации в связке. Это вызывает дополнительные издержки в использовании
public class User {       // Памяти и немного замедляет работу, но гарантирует, что поля не будут неожиданно изменены.
    @PositiveOrZero
    int id;
    @Email
    @NotNull
    @NotBlank
    String email;
    @NotBlank      // Аннотация для проверки на случай, если строка состоит из символов новой строки и возврата каретки
    @WithoutSpaces                            // Аннотация для проверки неравенства null и отсутствия пробелов в строке
    String login;
    String name;
    @NotImmortal                // Аннотация для проверки неравенства null и указания неверной даты (раньше 1900 года)
    @Past
    LocalDate birthday;

    @JsonCreator    // Специальный конструктор-creator для конвертирования в и из json с помощью библиотеки jackson
    private User(@JsonProperty("id") int id, @JsonProperty("email") String email, @JsonProperty("login") String login,
                @JsonProperty("name") String name,@JsonProperty("birthday") LocalDate birthday) {
        this.id = id;
        this.email = email;
        this.login = login;
        this.name = name;
        this.birthday = birthday;
    }

}