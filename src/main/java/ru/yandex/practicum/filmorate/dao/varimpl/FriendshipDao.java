package ru.yandex.practicum.filmorate.dao.varimpl;

import java.util.List;

import ru.yandex.practicum.filmorate.dao.FilmorateVariableStorageDao;
import ru.yandex.practicum.filmorate.model.data.FriendshipEntity;
import ru.yandex.practicum.filmorate.model.domain.FriendshipRequest;

public interface FriendshipDao extends FilmorateVariableStorageDao<FriendshipEntity, FriendshipRequest> {

    List<FriendshipRequest> getAllByUserId(long userId);

    List<FriendshipRequest> getAllByTwoUserId(long userId, long friendId);

    List<FriendshipRequest> getAllFriendshipRequests();

}