package ru.yandex.practicum.filmorate.model.service;

import lombok.Value;
import lombok.Builder;

@Value
@Builder(toBuilder = true)
public class Director {
    int id;
    String name;

}