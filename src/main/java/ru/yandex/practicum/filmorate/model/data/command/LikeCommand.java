package ru.yandex.practicum.filmorate.model.data.command;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class LikeCommand {
    private final long filmId;
    private final long userId;

}