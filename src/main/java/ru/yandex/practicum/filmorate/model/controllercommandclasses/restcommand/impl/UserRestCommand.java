package ru.yandex.practicum.filmorate.model.controllercommandclasses.restcommand.impl;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.constraints.*;

import java.time.LocalDate;
import java.util.Set;

import ru.yandex.practicum.filmorate.customvalidation.customannotations.WithoutSpaces;
import ru.yandex.practicum.filmorate.exception.UserValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.controllercommandclasses.restcommand.ObjectRestCommand;

@lombok.ToString
@lombok.EqualsAndHashCode
@lombok.NoArgsConstructor
public class UserRestCommand implements ObjectRestCommand<User> {
    @JsonProperty("id")
    @PositiveOrZero
    @lombok.Getter
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

    public UserRestCommand(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.login = user.getLogin();
        this.name = user.getName();
        this.birthday = user.getBirthday();
        this.friends = user.getFriends();
    }

    @Override
    public User convertToDomainObject() throws UserValidationException {
        User user = User.builder()
                .id(this.id)
                .email(this.email)
                .login(this.login)
                .name(this.name)
                .birthday(this.birthday)
                .build();
        if (friends != null && !friends.isEmpty()) {
            this.friends.forEach(id -> user.getFriends().add(id));
        }
        return user;
    }

}