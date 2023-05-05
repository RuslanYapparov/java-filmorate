package ru.yandex.practicum.filmorate.model.data;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;

@Value
@Builder(toBuilder = true)
public class UserEntity {
    long id;
    String email;
    String login;
    String name;
    LocalDate birthday;

}