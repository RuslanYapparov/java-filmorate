package ru.yandex.practicum.filmorate.model.service;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;
import java.util.Set;

@Value
@Builder(toBuilder = true)
public class User {
    long id;
    String email;
    String login;
    String name;
    LocalDate birthday;
    Set<Long> friends;

}