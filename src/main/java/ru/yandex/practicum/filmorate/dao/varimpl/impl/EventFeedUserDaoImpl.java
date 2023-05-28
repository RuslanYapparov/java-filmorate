package ru.yandex.practicum.filmorate.dao.varimpl.impl;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.varimpl.EventFeedUserDao;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundInStorageException;
import ru.yandex.practicum.filmorate.model.data.EventFeedEntity;
import ru.yandex.practicum.filmorate.model.data.ReviewEntity;
import ru.yandex.practicum.filmorate.model.service.EventFeed;

import java.sql.Timestamp;
import java.util.List;

@Repository
@Qualifier("eventFeedUserRepository")
public class EventFeedUserDaoImpl extends EventFeedDaoImpl implements EventFeedUserDao {
    private final ReviewDaoImpl reviewDao;

    public EventFeedUserDaoImpl(JdbcTemplate jdbcTemplate, ReviewDaoImpl reviewDao) {

        super(jdbcTemplate);
        this.reviewDao = reviewDao;
    }

    @Override
    public List<EventFeedEntity> getAllEventsByUserId(long userId) {

        sql = "select * from events where user_id = ? order by timestamp_event desc";

        try {
            return jdbcTemplate.query(sql, objectEntityRowMapper, userId);
        } catch (DataRetrievalFailureException exception) {
            throw new ObjectNotFoundInStorageException(String.format("Пользователь с идентификатором %d не оставлял "
                    + "событий в базе данных приложения", userId));
        }
    }

    @Override
    public EventFeedEntity saveEventWhenRemoveReview(long reviewId) {
        ReviewEntity review = reviewDao.getById(reviewId);
        EventFeed eventFeed = EventFeed.builder()
                .timestamp(new Timestamp(System.currentTimeMillis()).getTime())
                .userId(review.getUserId())
                .eventType("REVIEW")
                .operation("REMOVE")
                .entityId(review.getReviewId())
                .build();
        return super.save(eventFeed);
    }
}
