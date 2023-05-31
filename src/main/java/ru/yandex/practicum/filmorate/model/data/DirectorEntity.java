package ru.yandex.practicum.filmorate.model.data;

import lombok.Builder;
import lombok.Value;

@Value
@Builder(toBuilder = true)
public class DirectorEntity {
    int id;
    String name;

}