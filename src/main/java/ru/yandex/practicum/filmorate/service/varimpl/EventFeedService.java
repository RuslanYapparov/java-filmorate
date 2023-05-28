package ru.yandex.practicum.filmorate.service.varimpl;

import ru.yandex.practicum.filmorate.model.data.EventFeedEntity;
import ru.yandex.practicum.filmorate.model.presentation.restcommand.EventFeedRestCommand;
import ru.yandex.practicum.filmorate.model.service.EventFeed;
import ru.yandex.practicum.filmorate.service.CrudService;

import java.util.List;

public interface EventFeedService extends CrudService<EventFeed, EventFeedRestCommand> {

    List<EventFeed> getAllEventsByUserId(long userId);

    EventFeed saveEventWhenRemoveReview(long reviewId);
}
