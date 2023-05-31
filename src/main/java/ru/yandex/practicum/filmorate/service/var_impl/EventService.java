package ru.yandex.practicum.filmorate.service.var_impl;

import ru.yandex.practicum.filmorate.model.service.Event;
import ru.yandex.practicum.filmorate.model.service.EventOperation;
import ru.yandex.practicum.filmorate.model.service.EventType;
import ru.yandex.practicum.filmorate.service.ReadConstantObjectService;

import java.util.List;

public interface EventService extends ReadConstantObjectService<Event> {

    List<Event> getAllEventsByUserId(long userId);

    void save(long userId, EventType type, EventOperation operation, long entityId);

}