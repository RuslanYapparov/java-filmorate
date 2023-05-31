package ru.yandex.practicum.filmorate.controller.servicecontrollers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.mapper.ReviewMapper;
import ru.yandex.practicum.filmorate.model.presentation.restview.ReviewRestView;
import ru.yandex.practicum.filmorate.service.varimpl.ReviewService;

import javax.validation.constraints.Positive;
import java.util.List;
import java.util.stream.Collectors;

@Validated
@RestController
@RequestMapping("/reviews")
@Slf4j
@RequiredArgsConstructor
public class ReviewServiceController {
    private final ReviewService reviewService;
    private final ReviewMapper reviewMapper;

    @GetMapping
    public List<ReviewRestView> getAllByFilmId(
            @RequestParam(name = "filmId", defaultValue = "-1", required = false) long filmId,
            @RequestParam(name = "count", defaultValue = "10", required = false) Integer count) {
        log.debug("Запрошен список всех отзывов фильма с идентификатором '{}'. Если идентификатор фильма '-1', " +
                        "то запрос всех отзывов", filmId);
        return reviewService.getAllReviewsByFilmIdAndCount(filmId, count).stream()
                .map(reviewMapper::toRestView)
                .collect(Collectors.toList());
    }

    @PutMapping("/{review_id}/like/{userId}")
    public void addLike(@PathVariable("review_id") @Positive long reviewId,
                            @PathVariable("userId") @Positive long userId) {
        log.debug("Пользователь с идентификатором '{}' ставит лайк отзыву с идентификатором '{}'", userId, reviewId);
        reviewService.addLike(reviewId, userId);
    }

    @PutMapping("/{review_id}/dislike/{userId}")
    public void addDislike(@PathVariable("review_id") @Positive long reviewId,
                               @PathVariable("userId") @Positive long userId) {
        log.debug("Пользователь с идентификатором '{}' ставит дизлайк отзыву с идентификатором '{}'", userId, reviewId);
        reviewService.addDislike(reviewId, userId);
    }

    @DeleteMapping("/{review_id}/like/{userId}")
    public void deleteLike(@PathVariable("review_id") @Positive long reviewId,
                           @PathVariable("userId") @Positive long userId) {
        log.debug("Пользователь с идентификатором '{}' удаляет лайк у отзыва с идентификатором '{}'", userId, reviewId);
        reviewService.deleteLike(reviewId, userId);
    }

    @DeleteMapping("/{review_id}/dislike/{userId}")
    public void deleteDislike(@PathVariable("review_id") @Positive long reviewId,
                              @PathVariable("userId") @Positive long userId) {
        log.debug("Пользователь с идентификатором '{}' удаляет дизлайк у отзыва с идентификатором '{}'", userId,
                reviewId);
        reviewService.deleteDislike(reviewId, userId);
    }

}