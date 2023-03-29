package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import ru.yandex.practicum.filmorate.customvalidation.customannotations.WithoutSpaces;

import javax.validation.constraints.*;
import java.time.LocalDate;

@lombok.Value
@lombok.Builder(toBuilder = true)
public class User {
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
    @PastOrPresent
    @NotNull
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