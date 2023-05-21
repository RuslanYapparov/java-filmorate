package ru.yandex.practicum.filmorate.model.service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
public class Director {
    private final int id;
    @Setter
    private String name;

}