package ru.yandex.practicum.filmorate.model.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class FriendshipRequest {
    private final long userId;
    private final long friendId;

}