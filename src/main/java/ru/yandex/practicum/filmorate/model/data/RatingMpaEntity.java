package ru.yandex.practicum.filmorate.model.data;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class RatingMpaEntity {
    private final int id;
    private final String name;
    private final String description;

}