package ru.yandex.practicum.filmorate.model.data;

import lombok.Builder;
import lombok.Value;

@Value
@Builder(toBuilder = true)
public class GenreEntity {
    int id;
    String name;

}