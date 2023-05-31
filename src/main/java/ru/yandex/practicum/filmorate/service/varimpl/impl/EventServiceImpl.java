package ru.yandex.practicum.filmorate.service.varimpl.impl;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.varimpl.EventDao;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundInStorageException;
import ru.yandex.practicum.filmorate.mapper.EventMapper;
import ru.yandex.practicum.filmorate.model.service.Event;
import ru.yandex.practicum.filmorate.model.service.EventOperation;
import ru.yandex.practicum.filmorate.model.service.EventType;
import ru.yandex.practicum.filmorate.service.varimpl.*;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EventServiceImpl implements EventService {
    private final EventDao eventDao;
    private final EventMapper eventMapper;

    public EventServiceImpl(EventDao eventDao, EventMapper eventMapper) {
        this.eventDao = eventDao;
        this.eventMapper = eventMapper;
    }

    @Override
    public int getQuantity() {
        return eventDao.getQuantity();
    }

    @Override
    public void save(long userId, EventType type, EventOperation operation, long entityId) {
        eventDao.save(userId, type, operation, entityId);
    }

    @Override
    public List<Event> getAll() {
        return eventDao.getAll().stream()
                .map(eventMapper::fromDbEntity)
                .collect(Collectors.toList());
    }

    @Override
    public Event getById(long eventId) throws ObjectNotFoundInStorageException {
        return eventMapper.fromDbEntity(eventDao.getById(eventId));
    }

    @Override
    public List<Event> getAllEventsByUserId(long userId) throws ObjectNotFoundInStorageException {
        return eventDao.getAllEventsByUserId(userId).stream()
                .map(eventMapper::fromDbEntity)
                .sorted(Comparator.comparing(Event::getTimestamp))
                .collect(Collectors.toList());
    }

}