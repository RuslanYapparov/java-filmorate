package ru.yandex.practicum.filmorate.service.varimpl.impl;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.FilmorateVariableStorageDao;
import ru.yandex.practicum.filmorate.dao.varimpl.ReviewDao;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundInStorageException;
import ru.yandex.practicum.filmorate.mapper.ReviewMapper;
import ru.yandex.practicum.filmorate.model.data.ReviewEntity;
import ru.yandex.practicum.filmorate.model.presentation.restcommand.ReviewRestCommand;
import ru.yandex.practicum.filmorate.model.service.Review;
import ru.yandex.practicum.filmorate.model.service.User;
import ru.yandex.practicum.filmorate.service.varimpl.CrudServiceImpl;
import ru.yandex.practicum.filmorate.service.varimpl.FilmService;
import ru.yandex.practicum.filmorate.service.varimpl.ReviewService;
import ru.yandex.practicum.filmorate.service.varimpl.UserService;

@Service
@Qualifier("reviewService")
public class ReviewServiceImpl extends CrudServiceImpl<Review, ReviewEntity, ReviewRestCommand> implements ReviewService {
    @Qualifier("userService")
    private final UserService userService;
    @Qualifier("filmService")
    private final FilmService filmService;

    @Qualifier("reviewRepository")
    private final ReviewDao reviewDao;
    private final ReviewMapper reviewMapper;


    public ReviewServiceImpl(@Qualifier("reviewRepository")
                             FilmorateVariableStorageDao<ReviewEntity, Review> objectDao,
                             UserService userService,
                             FilmService filmService,
                             ReviewDao reviewDao,
                             ReviewMapper reviewMapper) {
        super(objectDao);
        this.userService = userService;
        this.filmService = filmService;
        this.reviewDao = reviewDao;
        this.reviewMapper = reviewMapper;
        this.objectFromDbEntityMapper = reviewMapper::fromDbEntity;
        this.objectFromRestCommandMapper = reviewMapper::fromRestCommand;
    }

    @Override
    public Review getById(long id) throws ObjectNotFoundInStorageException {
        return super.getById(id);
    }


    @Override
    public void addLike(long reviewId, long userId) {
        reviewDao.addLike(reviewId, userId);
    }

    @Override
    public void addDislike(long reviewId, long userId) {
        reviewDao.addDislike(reviewId, userId);
    }

    @Override
    public void deleteLike(long reviewId, long userId) {
        reviewDao.deleteLike(reviewId, userId);

    }

    @Override
    public void deleteDislike(long reviewId, long userId) {
        reviewDao.deleteDislike(reviewId, userId);
    }
}