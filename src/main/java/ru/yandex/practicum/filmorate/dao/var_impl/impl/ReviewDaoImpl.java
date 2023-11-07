package ru.yandex.practicum.filmorate.dao.var_impl.impl;

import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dao.var_impl.FilmorateVariableStorageDaoImpl;
import ru.yandex.practicum.filmorate.exception.InternalLogicException;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundInStorageException;
import ru.yandex.practicum.filmorate.model.data.ReviewEntity;
import ru.yandex.practicum.filmorate.model.service.Review;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Repository
@Primary
public class ReviewDaoImpl extends FilmorateVariableStorageDaoImpl<ReviewEntity, Review> {

    public ReviewDaoImpl(JdbcTemplate template) {
        super(template);
        this.type = "film_review";
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
        long reviewId = Optional.ofNullable(keyHolder.getKey())
                .orElseThrow(() -> new InternalLogicException("Произошла непредвиденная ошбика сохранения отзыва '\n" +
                        content + "\n'. Пожалуйста, повторите попытку. Если ошибка повторится, пожалуйста, " +
                        "свяжитесь с разработчиками приложения"))
                .longValue();
        return this.getById(reviewId);
    }

    @Override
    public ReviewEntity update(Review review) throws ObjectNotFoundInStorageException {
        sql = "update film_reviews set content = ?, is_positive = ? where film_review_id = ?";
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

    @Override
    public List<ReviewEntity> getAll() {
        sql = String.format("select * from %ss order by useful desc", type);
        return jdbcTemplate.query(sql, objectEntityRowMapper);
    }

}