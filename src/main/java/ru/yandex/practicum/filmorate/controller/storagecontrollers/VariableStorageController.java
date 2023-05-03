package ru.yandex.practicum.filmorate.controller.storagecontrollers;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

public interface VariableStorageController<TRC, TRV> extends ConstantStorageController<TRV> {

    TRV post(@Valid TRC command);

    TRV put(@Valid TRC command);

    void deleteAll();

    TRV deleteOneById(@Positive long id);

}