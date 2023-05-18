package ru.yandex.practicum.filmorate.model.service;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class FilmGenreCommand {
    private final long filmId;
    private final Genre genre;

}