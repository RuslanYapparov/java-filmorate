package ru.yandex.practicum.filmorate.model.presentation.restview;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@AllArgsConstructor
public class ReviewRestView {
    @JsonProperty("reviewId")
    long reviewId;
    @JsonProperty("content")
    String content;
    @JsonProperty("isPositive")
    boolean isPositive;
    @JsonProperty("userId")
    long userId;
    @JsonProperty("filmId")
    long filmId;
    @JsonProperty("useful")
    int useful;
}