package ru.yandex.practicum.filmorate.model.restinteractionmodel.restcommand;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.constraints.*;

import java.time.LocalDate;
import java.util.Set;

import ru.yandex.practicum.filmorate.customvalidation.customannotations.WithoutSpaces;

@lombok.Data
public class UserRestCommand {
    @JsonProperty("id")
    @PositiveOrZero
    long id;
    @JsonProperty("email")
    @Email
    @NotNull
    @NotBlank
    String email;
    @JsonProperty("login")
    @NotBlank      // Аннотация для проверки на случай, если строка состоит из символов новой строки и возврата каретки
    @WithoutSpaces                            // Аннотация для проверки неравенства null и отсутствия пробелов в строке
    String login;
    @JsonProperty("name")
    String name;
    @JsonProperty("birthday")
    @PastOrPresent
    @NotNull
    LocalDate birthday;
    @JsonProperty(value = "friends")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    Set<Long> friends;

}