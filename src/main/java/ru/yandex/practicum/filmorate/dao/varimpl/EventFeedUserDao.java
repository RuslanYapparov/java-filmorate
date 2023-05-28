package ru.yandex.practicum.filmorate.dao.varimpl;

import ru.yandex.practicum.filmorate.model.data.EventFeedEntity;

import java.util.List;

public interface EventFeedUserDao {

    List<EventFeedEntity> getAllEventsByUserId(long userId);

    EventFeedEntity saveEventWhenRemoveReview(long reviewId);
}
