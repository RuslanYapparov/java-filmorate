package ru.yandex.practicum.filmorate.controller.storagecontrollers.constimpl;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.validation.constraints.Positive;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import ru.yandex.practicum.filmorate.controller.storagecontrollers.ConstantStorageController;
import ru.yandex.practicum.filmorate.model.service.Genre;
import ru.yandex.practicum.filmorate.model.presentation.restview.GenreRestView;
import ru.yandex.practicum.filmorate.service.ReadConstantObjectService;

@Validated
@RestController
@Slf4j
@RequestMapping("/genres")
@RequiredArgsConstructor
public class GenreStorageControllerImpl implements ConstantStorageController<GenreRestView> {
    @Qualifier("genreService")
    private final ReadConstantObjectService<Genre> genreService;
    private final Function<Genre, GenreRestView> toRestViewConverter =
            genre -> new GenreRestView(genre.getId(), genre.getByRus());

    @Override
    @GetMapping
    public List<GenreRestView> getAll() {
        log.debug("Запрошен список всех жанров. Количество сохраненных видов жанров: {}",
                genreService.getQuantity());
        return genreService.getAll().stream()
                .map(toRestViewConverter)
                .collect(Collectors.toList());
    }

    @Override
    @GetMapping("{genre_id}")
    public GenreRestView getOneById(@PathVariable(value = "genre_id") @Positive long genreId) {
        Genre genre = genreService.getById(genreId);
        log.debug("Запрошен жанр с идентификатором {}. Жанр найден и отправлен клиенту", genre.getId());
        return toRestViewConverter.apply(genre);
    }

}