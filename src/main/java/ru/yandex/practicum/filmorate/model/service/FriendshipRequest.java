package ru.yandex.practicum.filmorate.model.service;

import lombok.Builder;
import lombok.Value;

@Value
@Builder(toBuilder = true)
public class FriendshipRequest {
    long userId;
    long friendId;

}