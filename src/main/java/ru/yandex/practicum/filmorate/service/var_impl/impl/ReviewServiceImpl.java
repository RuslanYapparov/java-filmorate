package ru.yandex.practicum.filmorate.service.var_impl.impl;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.FilmorateVariableStorageDao;
import ru.yandex.practicum.filmorate.dao.var_impl.ReviewLikeDao;
import ru.yandex.practicum.filmorate.exception.BadRequestBodyException;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundInStorageException;
import ru.yandex.practicum.filmorate.mapper.ReviewMapper;
import ru.yandex.practicum.filmorate.model.data.ReviewEntity;
import ru.yandex.practicum.filmorate.model.presentation.rest_command.ReviewRestCommand;
import ru.yandex.practicum.filmorate.model.service.EventOperation;
import ru.yandex.practicum.filmorate.model.service.EventType;
import ru.yandex.practicum.filmorate.model.service.Review;
import ru.yandex.practicum.filmorate.service.var_impl.*;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReviewServiceImpl extends CrudServiceImpl<Review, ReviewEntity, ReviewRestCommand>
        implements ReviewService {
    private final UserService userService;
    private final EventService eventService;
    private final FilmService filmService;
    private final ReviewLikeDao reviewLikeDao;
    private final ReviewMapper reviewMapper;

    public ReviewServiceImpl(FilmorateVariableStorageDao<ReviewEntity, Review> objectDao,
                             UserService userService,
                             EventService eventService,
                             FilmService filmService,
                             ReviewLikeDao reviewLikeDao,
                             ReviewMapper reviewMapper) {
        super(objectDao);
        this.userService = userService;
        this.eventService = eventService;
        this.filmService = filmService;
        this.reviewLikeDao = reviewLikeDao;
        this.reviewMapper = reviewMapper;
        this.objectFromDbEntityMapper = reviewMapper::fromDbEntity;
        this.objectFromRestCommandMapper = reviewMapper::fromRestCommand;
    }

    @Override
    public Review save(ReviewRestCommand reviewRestCommand) {
        long userId = reviewRestCommand.getUserId();
        long filmId = reviewRestCommand.getFilmId();
        if (userId == 0 || filmId == 0) {
            throw new BadRequestBodyException("Не указан обязательный параметр userId и(или) filmId в теле запроса, " +
                    "либо указан 0");
        }
        userService.getById(userId);
        filmService.getById(filmId);
        ReviewEntity reviewEntity = objectDao.save(objectFromRestCommandMapper.apply(reviewRestCommand));
        eventService.save(reviewEntity.getUserId(), EventType.REVIEW, EventOperation.ADD, reviewEntity.getReviewId());
        return objectFromDbEntityMapper.apply(reviewEntity);
    }

    @Override
    public Review update(ReviewRestCommand reviewRestCommand) throws ObjectNotFoundInStorageException {
        Review review = super.update(reviewRestCommand);
        eventService.save(review.getUserId(), EventType.REVIEW, EventOperation.UPDATE, review.getReviewId());
        return review;
    }

    @Override
    public Review deleteById(long reviewId) throws ObjectNotFoundInStorageException {
        Review review = super.deleteById(reviewId);
        eventService.save(review.getUserId(), EventType.REVIEW, EventOperation.REMOVE, review.getReviewId());
        return review;
    }

    @Override
    public List<Review> getAllReviewsByFilmIdAndCount(long filmId, int count) {
        return reviewLikeDao.getAllByFilmId(filmId, count).stream()
                .map(reviewMapper::fromDbEntity)
                .collect(Collectors.toList());
    }

    @Override
    public void addLike(long reviewId, long userId) {
        reviewLikeDao.addLike(reviewId, userId);
    }

    @Override
    public void addDislike(long reviewId, long userId) {
        reviewLikeDao.addDislike(reviewId, userId);
    }

    @Override
    public void deleteLike(long reviewId, long userId) {
        reviewLikeDao.deleteLike(reviewId, userId);
    }

    @Override
    public void deleteDislike(long reviewId, long userId) {
        reviewLikeDao.deleteDislike(reviewId, userId);
    }

}