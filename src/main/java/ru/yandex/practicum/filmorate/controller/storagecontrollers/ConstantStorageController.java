package ru.yandex.practicum.filmorate.controller.storagecontrollers;

import javax.validation.constraints.Positive;

import java.util.List;

public interface ConstantStorageController<TRV> {
                                       //  TRV - TypeRestView - тип представления объекта во внешней среде
    List<TRV> getAll();

    TRV getOneById(@Positive long id);

}