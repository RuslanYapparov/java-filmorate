package ru.yandex.practicum.filmorate.service.var_impl;

import ru.yandex.practicum.filmorate.model.presentation.rest_command.ReviewRestCommand;
import ru.yandex.practicum.filmorate.model.service.Review;
import ru.yandex.practicum.filmorate.service.CrudService;

import java.util.List;

public interface ReviewService extends CrudService<Review, ReviewRestCommand> {

    List<Review> getAllReviewsByFilmIdAndCount(long filmId, int count);

    void addLike(long reviewId, long userId);

    void addDislike(long reviewId, long userId);

    void deleteLike(long reviewId, long userId);

    void deleteDislike(long reviewId, long userId);
}