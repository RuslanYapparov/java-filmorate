package ru.yandex.practicum.filmorate.dao.var_impl;

import ru.yandex.practicum.filmorate.model.data.ReviewEntity;

import java.util.List;

public interface ReviewLikeDao {

    List<ReviewEntity> getAllByFilmId(long filmId, int count);

    void addLike(long reviewId, long userId);

    void addDislike(long reviewId, long userId);

    void deleteLike(long reviewId, long userId);

    void deleteDislike(long reviewId, long userId);
}