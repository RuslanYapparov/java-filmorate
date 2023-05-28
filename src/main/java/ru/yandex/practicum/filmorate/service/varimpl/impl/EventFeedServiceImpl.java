package ru.yandex.practicum.filmorate.service.varimpl.impl;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.FilmorateVariableStorageDao;
import ru.yandex.practicum.filmorate.dao.varimpl.EventFeedUserDao;
import ru.yandex.practicum.filmorate.mapper.EventFeedMapper;
import ru.yandex.practicum.filmorate.model.data.EventFeedEntity;
import ru.yandex.practicum.filmorate.model.presentation.restcommand.EventFeedRestCommand;
import ru.yandex.practicum.filmorate.model.service.EventFeed;
import ru.yandex.practicum.filmorate.service.varimpl.*;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Qualifier("eventFeedService")
public class EventFeedServiceImpl extends CrudServiceImpl<EventFeed, EventFeedEntity, EventFeedRestCommand>
        implements EventFeedService {
    @Qualifier("eventFeedUserRepository")
    private final EventFeedUserDao eventFeedUserDao;
    private final EventFeedMapper eventFeedMapper;

    public EventFeedServiceImpl(@Qualifier("eventFeedRepository")
                                FilmorateVariableStorageDao<EventFeedEntity, EventFeed> objectDao,
                                EventFeedUserDao eventFeedUserDao,
                                EventFeedMapper eventFeedMapper) {
        super(objectDao);
        this.eventFeedUserDao = eventFeedUserDao;
        this.eventFeedMapper = eventFeedMapper;
        this.objectFromDbEntityMapper = eventFeedMapper::fromDbEntity;
    }

    @Override
    public List<EventFeed> getAllEventsByUserId(long userId) {
        return eventFeedUserDao.getAllEventsByUserId(userId).stream()
                .map(objectFromDbEntityMapper)
                .collect(Collectors.toList());
    }

    @Override
    public EventFeed saveEventWhenRemoveReview(long reviewId) {
        return eventFeedMapper.fromDbEntity(eventFeedUserDao.saveEventWhenRemoveReview(reviewId));
    }
}
