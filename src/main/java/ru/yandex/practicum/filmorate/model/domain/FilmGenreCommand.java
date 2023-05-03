package ru.yandex.practicum.filmorate.model.domain;

@lombok.AllArgsConstructor
@lombok.Getter
public class FilmGenreCommand {
    private final long filmId;
    private final Genre genre;

}