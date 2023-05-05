package ru.yandex.practicum.filmorate.model.data;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class FriendshipEntity {
    private final long userId;
    private final long friendId;
    private final boolean isConfirmed;

}