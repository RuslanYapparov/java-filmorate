package ru.yandex.practicum.filmorate.controller.storage_controllers;

import javax.validation.constraints.Positive;

import java.util.List;

public interface ConstantStorageController<V> {
                                       //  V - RestView - тип представления объекта во внешней среде
    List<V> getAll();

    V getOneById(@Positive long id);

}