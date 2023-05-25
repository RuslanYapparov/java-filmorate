package ru.yandex.practicum.filmorate.model.presentation.restcommand;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ReviewRestCommand {
    @JsonProperty("reviewId")
    @PositiveOrZero
    Long reviewId;
    @JsonProperty("content")
    @NotNull
    @NotBlank
    @Size(max = 1000)
    String content;
    @JsonProperty("isPositive")
    @NotNull
    Boolean isPositive;
    @JsonProperty("userId")
    @PositiveOrZero
    Long userId;
    @JsonProperty("filmId")
    @PositiveOrZero
    Long filmId;
    @JsonProperty("useful")
    @PositiveOrZero
    Long useful;
}