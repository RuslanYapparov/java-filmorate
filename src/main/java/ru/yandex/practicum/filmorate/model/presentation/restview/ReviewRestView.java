package ru.yandex.practicum.filmorate.model.presentation.restview;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@EqualsAndHashCode
@AllArgsConstructor
@Builder
@NoArgsConstructor
@Getter
public class ReviewRestView {
    @JsonProperty("reviewId")
    Long reviewId;
    @JsonProperty("content")
    String content;
    @JsonProperty("isPositive")
    Boolean isPositive;
    @JsonProperty("userId")
    Long userId;
    @JsonProperty("filmId")
    Long filmId;
    @JsonProperty("useful")
    Long useful;

}
