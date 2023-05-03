package ru.yandex.practicum.filmorate.controller.storagecontrollers;

import javax.validation.constraints.Positive;

import java.util.List;

public interface ConstantStorageController<V> {
                                       //  V - TypeRestView - тип представления объекта во внешней среде
    List<V> getAll();

    V getOneById(@Positive long id);

}