package ru.yandex.practicum.filmorate.model.presentation.rest_command;

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
    long reviewId;
    @JsonProperty("content")
    @NotNull
    @NotBlank
    @Size(max = 10000)
    String content;
    @JsonProperty("isPositive")
    @NotNull
    Boolean isPositive;
    @JsonProperty("userId")
    long userId;
    @JsonProperty("filmId")
    long filmId;
    @JsonProperty("useful")
    @PositiveOrZero
    int useful;

    public boolean getIsPositive() {
        return isPositive;
    }
}