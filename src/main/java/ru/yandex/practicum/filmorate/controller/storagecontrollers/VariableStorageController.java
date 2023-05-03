package ru.yandex.practicum.filmorate.controller.storagecontrollers;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

public interface VariableStorageController<C, V> extends ConstantStorageController<V> {

    V post(@Valid C command);

    V put(@Valid C command);

    void deleteAll();

    V deleteOneById(@Positive long id);

}