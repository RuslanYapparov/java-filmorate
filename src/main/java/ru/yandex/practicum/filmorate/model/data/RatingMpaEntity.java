package ru.yandex.practicum.filmorate.model.data;

import lombok.Builder;
import lombok.Value;

@Value
@Builder(toBuilder = true)
public class RatingMpaEntity {
    int id;
    String name;
    String description;

}