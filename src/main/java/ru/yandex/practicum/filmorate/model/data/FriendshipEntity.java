package ru.yandex.practicum.filmorate.model.data;

@lombok.AllArgsConstructor
@lombok.Getter
public class FriendshipEntity {
    private final long userId;
    private final long friendId;
    private final boolean isConfirmed;

}