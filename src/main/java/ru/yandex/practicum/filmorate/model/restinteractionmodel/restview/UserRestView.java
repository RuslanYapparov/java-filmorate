package ru.yandex.practicum.filmorate.model.restinteractionmodel.restview;

import java.time.LocalDate;
import java.util.Set;

@lombok.Data
@lombok.AllArgsConstructor
@lombok.NoArgsConstructor
public class UserRestView {
    long id;
    String email;
    String login;
    String name;
    LocalDate birthday;
    Set<Long> friends;

}