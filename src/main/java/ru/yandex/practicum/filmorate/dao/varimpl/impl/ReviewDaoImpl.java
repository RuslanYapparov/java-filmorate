package ru.yandex.practicum.filmorate.dao.varimpl.impl;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.varimpl.FilmorateVariableStorageDaoImpl;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundInStorageException;
import ru.yandex.practicum.filmorate.model.data.ReviewEntity;
import ru.yandex.practicum.filmorate.model.service.EventFeed;
import ru.yandex.practicum.filmorate.model.service.Review;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.List;

@Repository
@Primary
@Qualifier("reviewRepository")
public class ReviewDaoImpl extends FilmorateVariableStorageDaoImpl<ReviewEntity, Review> {

    private final EventFeedDaoImpl eventFeedDao;

    public ReviewDaoImpl(JdbcTemplate template, EventFeedDaoImpl eventFeedDao) {
        super(template);
        this.type = "film_review";
        this.eventFeedDao = eventFeedDao;
        this.objectEntityRowMapper = (resultSet, rowNumber) ->
                ReviewEntity.builder()
                        .reviewId(resultSet.getLong("film_review_id"))
                        .content(resultSet.getString("content"))
                        .isPositive(resultSet.getBoolean("is_positive"))
                        .userId(resultSet.getLong("user_id"))
                        .filmId(resultSet.getLong("film_id"))
                        .useful(resultSet.getInt("useful"))
                        .build();
    }

    @Override
    public ReviewEntity save(Review review) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        String content = review.getContent();
        boolean isPositive = review.getIsPositive();
        long userId = review.getUserId();
        long filmId = review.getFilmId();
        long useful = review.getUseful();
        sql = "insert into film_reviews (content, is_positive, user_id, film_id, useful) values (?, ?, ?, ?, ?)";
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, content);
            ps.setBoolean(2, isPositive);
            ps.setLong(3, userId);
            ps.setLong(4, filmId);
            ps.setLong(5, useful);
            return ps;
        }, keyHolder);
        EventFeed eventFeed = EventFeed.builder()
                .timestamp(new Timestamp(System.currentTimeMillis()).getTime())
                .userId(userId)
                .eventType("REVIEW")
                .operation("ADD")
                .entityId(keyHolder.getKey().longValue())
                .build();
        eventFeedDao.save(eventFeed);
        return this.getById(keyHolder.getKey().longValue());
    }

    @Override
    public ReviewEntity update(Review review) throws ObjectNotFoundInStorageException {
        sql = "update film_reviews set content = ?, is_positive = ? where film_review_id = ?";
        try {
            jdbcTemplate.update(sql,
                    review.getContent(),
                    review.getIsPositive(),
                    review.getReviewId());
            EventFeed eventFeed = EventFeed.builder()
                    .timestamp(new Timestamp(System.currentTimeMillis()).getTime())
                    .userId(review.getUserId())
                    .eventType("REVIEW")
                    .operation("UPDATE")
                    .entityId(review.getReviewId())
                    .build();
            eventFeedDao.save(eventFeed);
            return this.getById(review.getReviewId());
        } catch (DataRetrievalFailureException exception) {
            throw new ObjectNotFoundInStorageException("Данные не могут быть обновлены, т.к. отзыв " +
                    "с указанным идентификатором не был сохранен");
        }
    }

    @Override
    public List<ReviewEntity> getAll() {
        sql = String.format("select * from %ss order by useful desc", type);
        return jdbcTemplate.query(sql, objectEntityRowMapper);
    }
}