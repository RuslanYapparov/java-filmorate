package ru.yandex.practicum.filmorate.model.data.command;

import lombok.Builder;
import lombok.Value;

@Value
@Builder(toBuilder = true)
public class LikeCommand {
    long filmId;
    long userId;

}