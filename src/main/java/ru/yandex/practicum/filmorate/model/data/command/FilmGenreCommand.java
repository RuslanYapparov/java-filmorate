package ru.yandex.practicum.filmorate.model.data.command;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class FilmGenreCommand {
    private final long filmId;
    private final int genreId;

}