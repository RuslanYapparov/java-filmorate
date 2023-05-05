package ru.yandex.practicum.filmorate.controller.storagecontrollers.constimpl;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.validation.constraints.Positive;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import ru.yandex.practicum.filmorate.controller.storagecontrollers.ConstantStorageController;
import ru.yandex.practicum.filmorate.model.domain.RatingMpa;
import ru.yandex.practicum.filmorate.model.presentation.restview.RatingMpaRestView;
import ru.yandex.practicum.filmorate.service.ReadConstantObjectService;

@Validated
@RestController
@Slf4j
@RequestMapping("/mpa")
@RequiredArgsConstructor
public class RatingMpaStorageControllerImpl implements ConstantStorageController<RatingMpaRestView> {
    @Qualifier("ratingService")
    private final ReadConstantObjectService<RatingMpa> ratingMpaService;
    private final Function<RatingMpa, RatingMpaRestView> toRestViewConverter =
            ratingMpa -> new RatingMpaRestView(ratingMpa.getId(), ratingMpa.getName());

    @Override
    @GetMapping
    public List<RatingMpaRestView> getAll() {
        log.debug("Запрошен список всех рейтингов. Количество сохраненных видов рейтингов: {}",
                ratingMpaService.getQuantity());
        return ratingMpaService.getAll().stream()
                .map(toRestViewConverter)
                .collect(Collectors.toList());
    }

    @Override
    @GetMapping("{rating_id}")
    public RatingMpaRestView getOneById(@PathVariable(value = "rating_id") @Positive long ratingId) {
        RatingMpa ratingMpa = ratingMpaService.getById(ratingId);
        log.debug("Запрошен рейтинг с идентификатором {}. Рейтинг найден и отправлен клиенту", ratingMpa.getId());
        return toRestViewConverter.apply(ratingMpa);
    }

}