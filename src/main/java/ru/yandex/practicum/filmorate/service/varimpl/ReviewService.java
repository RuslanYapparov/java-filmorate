package ru.yandex.practicum.filmorate.service.varimpl;

import ru.yandex.practicum.filmorate.model.presentation.restcommand.ReviewRestCommand;
import ru.yandex.practicum.filmorate.model.service.Review;
import ru.yandex.practicum.filmorate.service.CrudService;

public interface ReviewService extends CrudService<Review, ReviewRestCommand> {

    void addLike(long reviewId, long userId);

    void addDislike(long reviewId, long userId);

    void deleteLike(long reviewId, long userId);

    void deleteDislike(long reviewId, long userId);
}
