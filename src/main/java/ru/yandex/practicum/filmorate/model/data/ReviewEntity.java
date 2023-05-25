package ru.yandex.practicum.filmorate.model.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Value;

@Value
@Builder(toBuilder = true)
public class ReviewEntity {
    long reviewId;
    String content;
    boolean isPositive;
    long userId;
    long filmId;
    long useful;
}
