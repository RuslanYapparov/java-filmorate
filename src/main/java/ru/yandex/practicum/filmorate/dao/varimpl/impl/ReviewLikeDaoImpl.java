package ru.yandex.practicum.filmorate.dao.varimpl.impl;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.varimpl.ReviewLikeDao;
import ru.yandex.practicum.filmorate.exception.ObjectAlreadyExistsException;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundInStorageException;
import ru.yandex.practicum.filmorate.model.data.ReviewEntity;

import java.util.List;
import java.util.stream.Collectors;

@Repository
@Qualifier("reviewLikeRepository")
public class ReviewLikeDaoImpl extends ReviewDaoImpl implements ReviewLikeDao {

    public ReviewLikeDaoImpl(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    @Override
    public List<ReviewEntity> getAllByFilmId(long filmId, int count) {
        List<ReviewEntity> reviews;
        if (filmId == -1) {
            reviews = this.getAll();
        } else {
            sql = "select * from film_reviews where film_id = ? order by useful desc";
            reviews = jdbcTemplate.query(sql, objectEntityRowMapper, filmId);
        }
        if (reviews.size() > count) {
            reviews = reviews.stream().limit(count).collect(Collectors.toList());
        }
        return reviews;
    }

    @Override
    public void addLike(long reviewId, long userId) throws ObjectAlreadyExistsException {
        this.addEntityToDb(reviewId, userId, Boolean.TRUE);
    }

    @Override
    public void addDislike(long reviewId, long userId) throws ObjectAlreadyExistsException {
        this.addEntityToDb(reviewId, userId, Boolean.FALSE);
    }

    @Override
    public void deleteLike(long reviewId, long userId) throws ObjectNotFoundInStorageException {
        this.deleteEntityFromDb(reviewId, userId, Boolean.TRUE);
    }

    @Override
    public void deleteDislike(long reviewId, long userId) throws ObjectNotFoundInStorageException {
        this.deleteEntityFromDb(reviewId, userId, Boolean.FALSE);
    }

    private void addEntityToDb(long reviewId, long userId, boolean isPositive) throws ObjectAlreadyExistsException {
        sql = "select * from film_review_likes where film_review_id = ? and user_id = ? and is_like = ?";
        SqlRowSet friendshipRows = jdbcTemplate.queryForRowSet(sql, reviewId, userId, isPositive);
        if (friendshipRows.next()) {
            throw new ObjectAlreadyExistsException("Лайк(дизлайк) отзыву уже был проставлен ранее");
        }
        sql = "insert into film_review_likes (film_review_id, user_id, is_like) VALUES (?, ?, ?)";
        jdbcTemplate.update(sql, reviewId, userId, isPositive);
        if (isPositive) {
            this.increaseUsefulScore(reviewId);
        } else {
            this.decreaseUsefulScore(reviewId);
        }
    }

    private void deleteEntityFromDb(long reviewId, long userId, boolean isPositive)
            throws ObjectNotFoundInStorageException {
        sql = "delete from film_review_likes where film_review_id = ? and user_id = ?";
        try {
            jdbcTemplate.update(sql, reviewId, userId);
        } catch (DataRetrievalFailureException exception) {
            throw new ObjectNotFoundInStorageException("Лайк(дизлайк) для отзыва не был сохранен");
        }
        if (isPositive) {
            this.decreaseUsefulScore(reviewId);
        } else {
            this.increaseUsefulScore(reviewId);
        }
    }

    private void increaseUsefulScore(long reviewId) {
        sql = "update film_reviews set useful = useful + 1 where film_review_id = ?";
        jdbcTemplate.update(sql, reviewId);
    }

    private void decreaseUsefulScore(long reviewId) {
        sql = "update film_reviews set useful = useful - 1 where film_review_id = ?";
        jdbcTemplate.update(sql, reviewId);
    }

}