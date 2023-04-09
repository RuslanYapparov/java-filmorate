package ru.yandex.practicum.filmorate.controller.storagecontrollers.impl;

import ru.yandex.practicum.filmorate.model.controllercommandclasses.restcommand.impl.FilmRestCommand;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;

public interface FilmStorageController {

    List<FilmRestCommand> getAll();

    FilmRestCommand getOneById(@Positive long id);

    FilmRestCommand post(@Valid FilmRestCommand command);

    FilmRestCommand put(@Valid FilmRestCommand command);

    void deleteAll();

    FilmRestCommand deleteOneById(@Positive long id);

}