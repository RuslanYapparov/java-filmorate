package ru.yandex.practicum.filmorate.controller.storagecontrollers;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;

import ru.yandex.practicum.filmorate.model.dto.restcommand.UserRestCommand;
import ru.yandex.practicum.filmorate.model.dto.restview.UserRestView;

public interface UserStorageController {

    List<UserRestView> getAll();

    UserRestView getOneById(@Positive long id);

    UserRestView post(@Valid UserRestCommand command);

    UserRestView put(@Valid UserRestCommand command);

    void deleteAll();

    UserRestView deleteOneById(@Positive long id);

}