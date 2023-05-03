package ru.yandex.practicum.filmorate.model.data;

import java.time.LocalDate;

@lombok.Value
@lombok.Builder(toBuilder = true)
public class UserEntity {
    long id;
    String email;
    String login;
    String name;
    LocalDate birthday;

}