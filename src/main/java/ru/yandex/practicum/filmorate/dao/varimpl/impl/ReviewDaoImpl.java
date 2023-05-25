package ru.yandex.practicum.filmorate.dao.varimpl.impl;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.GetMapping;
import ru.yandex.practicum.filmorate.dao.varimpl.FilmorateVariableStorageDaoImpl;
import ru.yandex.practicum.filmorate.dao.varimpl.ReviewDao;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundInStorageException;
import ru.yandex.practicum.filmorate.model.data.ReviewEntity;
import ru.yandex.practicum.filmorate.model.service.Review;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@Qualifier("reviewRepository")
public class ReviewDaoImpl extends FilmorateVariableStorageDaoImpl<ReviewEntity, Review> implements ReviewDao {

    public ReviewDaoImpl(JdbcTemplate template) {
        super(template);
        this.type = "review";
        this.objectEntityRowMapper = (resultSet, rowNumber) ->
                ReviewEntity.builder()
                        .reviewId(resultSet.getLong("review_id"))
                        .content(resultSet.getString("content"))
                        .isPositive(resultSet.getBoolean("isPositive"))
                        .userId(resultSet.getLong("userId"))
                        .filmId(resultSet.getLong("filmId"))
                        .useful(resultSet.getLong("useful"))
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
        sql = "insert into film_reviews (content, isPositive, userId, filmId, useful) values (?, ?, ?, ?, ?)";
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, content);
            ps.setBoolean(2, isPositive);
            ps.setLong(3, userId);
            ps.setLong(4, filmId);
            ps.setLong(4, useful);
            return ps;
        }, keyHolder);
        return this.getById(keyHolder.getKey().longValue());
    }

    @Override
    public ReviewEntity update(Review review) throws ObjectNotFoundInStorageException {
        sql = "update film_reviews set content = ?, isPositive = ? where review_id = ?";
        try {
            jdbcTemplate.update(sql,
                    review.getContent(),
                    review.getIsPositive(),
                    review.getReviewId());
            return this.getById(review.getReviewId());
        } catch (DataRetrievalFailureException exception) {
            throw new ObjectNotFoundInStorageException("Данные не могут быть обновлены, т.к. отзыв " +
                    "с указанным идентификатором не был сохранен");
        }
    }

    @GetMapping
    public List<ReviewEntity> getAllByFilmId(long filmId, int count) {
        List<ReviewEntity> reviews = new ArrayList<>();
        if (filmId == -1) {
            reviews = getAll();
        } else {
            sql = "select * from film_reviews where review_id = ? order by useful desc";
        }
        if (reviews.size() > count) {
            reviews = reviews.stream().limit(count).collect(Collectors.toList());

        }
        return reviews;
    }

    @Override
    public void addLike(long reviewId, long userId) {
        addRating(reviewId, userId, Boolean.TRUE);
    }

    @Override
    public void addDislike(long reviewId, long userId) {
        addRating(reviewId, userId, Boolean.FALSE);
    }

    @Override
    public void deleteLike(long reviewId, long userId) {
        deleteRating(reviewId, userId);
    }

    @Override
    public void deleteDislike(long reviewId, long userId) {
        deleteRating(reviewId, userId);
    }

    private void addRating(long reviewId, long userId, boolean isPositive) {
        sql = "insert into film_review_likes (review_id, user_id, isLike) VALUES (?, ?, ?)";
        jdbcTemplate.update(sql, reviewId, userId, isPositive);
        increaseUsefulScore(reviewId);
    }

    private void deleteRating(long reviewId, long userId) {
        sql = "delete from film_review_ratings where review_id = ? and user_id = ?";
        jdbcTemplate.update(sql, reviewId, userId);
        decreaseUsefulScore(reviewId);
    }

    private void increaseUsefulScore(long reviewId) {
        sql = "update film_reviews set useful = useful + 1 where review_id = ?";
        jdbcTemplate.update(sql, reviewId);
    }

    private void decreaseUsefulScore(long reviewId) {
        sql = "update film_reviews set useful = useful - 1 where review_id = ?";
        jdbcTemplate.update(sql, reviewId);
    }

    /*@Override
    public ReviewEntity getById(long id) {
        return null;
    }

    @Override
    public List<ReviewEntity> getAll() {
        sql = "select * from reviews order by useful DESC";
        return jdbcTemplate.query(sql, objectEntityRowMapper);
    }

    @Override
    public ReviewCommand deleteById(long reviewId) {
        sql = "delete from likes where film_id = ? and user_id = ?";
        try {
            jdbcTemplate.update(sql, filmId, userId);
        } catch (DataRetrievalFailureException exception) {
            throw new ObjectNotFoundInStorageException(String.format("Пользователь с id%d не ставил лайк фильму с id%d",
                    userId, filmId));
        }
        return new LikeCommand(filmId, userId);
    }*/


}