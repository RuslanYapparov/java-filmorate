package ru.yandex.practicum.filmorate.model.service;

import lombok.Builder;
import lombok.Value;

@Value
@Builder(toBuilder = true)
public class Review {
    long reviewId;
    String content;
    boolean isPositive;
    long userId;
    long filmId;
    int useful;

    public boolean getIsPositive() {
        return isPositive;
    }
}