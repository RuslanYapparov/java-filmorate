package ru.yandex.practicum.filmorate.model.data;

@lombok.AllArgsConstructor
@lombok.Getter
public class LikeEntity {
    private final long filmId;
    private final long userId;

}