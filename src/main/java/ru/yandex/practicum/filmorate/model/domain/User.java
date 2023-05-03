package ru.yandex.practicum.filmorate.model.domain;

import java.time.LocalDate;
import java.util.Set;

@lombok.Value
@lombok.Builder(toBuilder = true)
public class User {
    long id;
    String email;
    String login;
    String name;
    LocalDate birthday;
    Set<Long> friends;

}