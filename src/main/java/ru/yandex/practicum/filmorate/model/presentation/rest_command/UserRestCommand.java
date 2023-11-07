package ru.yandex.practicum.filmorate.model.presentation.rest_command;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import javax.validation.constraints.*;

import java.time.LocalDate;
import java.util.Set;

import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.custom_validation.custom_annotations.WithoutSpaces;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class UserRestCommand {
    @JsonProperty("id")
    @PositiveOrZero
    private long id;
    @JsonProperty("email")
    @Email
    @NotNull
    @NotBlank
    private String email;
    @JsonProperty("login")
    @NotBlank
    @WithoutSpaces                            // Аннотация для проверки неравенства null и отсутствия пробелов в строке
    private String login;
    @JsonProperty("name")
    private String name;
    @JsonProperty("birthday")
    @PastOrPresent
    @NotNull
    private LocalDate birthday;
    @JsonProperty(value = "friends")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Set<Long> friends;

}