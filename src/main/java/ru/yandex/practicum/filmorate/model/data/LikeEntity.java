package ru.yandex.practicum.filmorate.model.data;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class LikeEntity {
    private final long filmId;
    private final long userId;

}