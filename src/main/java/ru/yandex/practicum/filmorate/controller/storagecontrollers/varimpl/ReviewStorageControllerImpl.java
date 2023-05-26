package ru.yandex.practicum.filmorate.controller.storagecontrollers.varimpl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.controller.storagecontrollers.VariableStorageController;
import ru.yandex.practicum.filmorate.mapper.ReviewMapper;
import ru.yandex.practicum.filmorate.model.presentation.restcommand.ReviewRestCommand;
import ru.yandex.practicum.filmorate.model.presentation.restview.ReviewRestView;
import ru.yandex.practicum.filmorate.model.service.Review;
import ru.yandex.practicum.filmorate.service.varimpl.ReviewService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;
import java.util.stream.Collectors;

@Validated
@RestController
@RequestMapping("/reviews")
@Slf4j
@RequiredArgsConstructor
public class ReviewStorageControllerImpl implements VariableStorageController<ReviewRestCommand, ReviewRestView> {
    @Qualifier("reviewService")
    private final ReviewService reviewService;
    private final ReviewMapper reviewMapper;

    @Override
    @GetMapping("{review_id}")
    public ReviewRestView getOneById(@PathVariable(value = "review_id") @Positive long reviewId) {
        Review review = reviewService.getById((int) reviewId);
        log.debug("Запрошен отзыв с идентификатором {}. Отзыв найден и отправлен клиенту", review.getReviewId());
        return reviewMapper.toRestView(review);
    }

    @Override
    @PostMapping
    public ReviewRestView post(@RequestBody @Valid ReviewRestCommand reviewRestCommand) {
        Review review = reviewService.save(reviewRestCommand);
        log.debug("Сохранен новый отзыв. Присвоен идентификатор {}", review.getReviewId());
        return reviewMapper.toRestView(review);
    }

    @Override
    @PutMapping
    public ReviewRestView put(@RequestBody ReviewRestCommand reviewRestCommand) {
        Review review = reviewService.update(reviewRestCommand);
        log.debug("Отзыв обновлён. Идентификатор отзыва: {}", review.getReviewId());
        return reviewMapper.toRestView(review);
    }

    @Override
    @DeleteMapping("{review_id}")
    public ReviewRestView deleteOneById(@PathVariable(value = "review_id") @Positive long reviewId) {
        Review review = reviewService.deleteById((int) reviewId);
        log.debug("Запрошено удаление отзыва с индентификатором '{}'. Отзыв удалён", review.getReviewId());
        return reviewMapper.toRestView(review);
    }

    @Override
    @DeleteMapping
    public void deleteAll() {
        log.debug("Удалены данные всех отзывов из хранилища");
        reviewService.deleteAll();
    }

    @Override
    @GetMapping("/all")
    public List<ReviewRestView> getAll() {
        log.debug("Запрошен список всех отзывов. Количество сохраненных отзывов: {}", reviewService.getQuantity());
        return reviewService.getAll().stream()
                .map(reviewMapper::toRestView)
                .collect(Collectors.toList());
    }

}
