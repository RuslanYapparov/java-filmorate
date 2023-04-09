package ru.yandex.practicum.filmorate.model.controllercommandclasses.restcommand;

@FunctionalInterface
public interface ObjectRestCommand<T> {

    T convertToDomainObject();

}