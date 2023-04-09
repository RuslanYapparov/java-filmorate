package ru.yandex.practicum.filmorate.controller.storagecontrollers.impl;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;

import ru.yandex.practicum.filmorate.model.controllercommandclasses.restcommand.impl.UserRestCommand;

public interface UserStorageController {

    List<UserRestCommand> getAll();

    UserRestCommand getOneById(@Positive long id);

    UserRestCommand post(@Valid UserRestCommand command);

    UserRestCommand put(@Valid UserRestCommand command);

    void deleteAll();

    UserRestCommand deleteOneById(@Positive long id);

}