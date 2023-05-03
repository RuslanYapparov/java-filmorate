package ru.yandex.practicum.filmorate.model.domain;

@lombok.AllArgsConstructor
@lombok.Getter
public class FriendshipRequest {
    private final long userId;
    private final long friendId;

}