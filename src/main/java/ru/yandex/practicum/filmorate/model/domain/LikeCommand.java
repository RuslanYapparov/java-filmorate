package ru.yandex.practicum.filmorate.model.domain;

@lombok.AllArgsConstructor
@lombok.Getter
public class LikeCommand {
    private final long filmId;
    private final long userId;

}