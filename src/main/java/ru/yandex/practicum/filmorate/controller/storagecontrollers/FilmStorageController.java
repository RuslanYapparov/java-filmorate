package ru.yandex.practicum.filmorate.controller.storagecontrollers;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;

import ru.yandex.practicum.filmorate.model.dto.restcommand.FilmRestCommand;
import ru.yandex.practicum.filmorate.model.dto.restview.FilmRestView;

public interface FilmStorageController {

    List<FilmRestView> getAll();

    FilmRestView getOneById(@Positive long id);

    FilmRestView post(@Valid FilmRestCommand command);

    FilmRestView put(@Valid FilmRestCommand command);

    void deleteAll();

    FilmRestView deleteOneById(@Positive long id);

}