package ru.yandex.practicum.filmorate.dao.varimpl;

import ru.yandex.practicum.filmorate.dao.FilmorateConstantStorageDao;
import ru.yandex.practicum.filmorate.model.data.EventEntity;
import ru.yandex.practicum.filmorate.model.service.Event;
import ru.yandex.practicum.filmorate.model.service.EventOperation;
import ru.yandex.practicum.filmorate.model.service.EventType;

import java.util.List;

public interface EventDao extends FilmorateConstantStorageDao<EventEntity> {

    EventEntity save(long userId, EventType type, EventOperation operation, long entityId);

    List<EventEntity> getAllEventsByUserId(long userId);

}