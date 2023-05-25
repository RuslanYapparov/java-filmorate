package ru.yandex.practicum.filmorate.model.data.command;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ReviewCommand {
    private final long reviewId;
    private final long userId;
    private final long filmId;
}