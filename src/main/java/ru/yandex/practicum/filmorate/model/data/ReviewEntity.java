package ru.yandex.practicum.filmorate.model.data;

import lombok.Builder;
import lombok.Value;

@Value
@Builder(toBuilder = true)
public class ReviewEntity {
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