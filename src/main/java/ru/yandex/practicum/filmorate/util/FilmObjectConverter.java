package ru.yandex.practicum.filmorate.util;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.restinteractionmodel.restcommand.FilmRestCommand;
import ru.yandex.practicum.filmorate.model.restinteractionmodel.restview.FilmRestView;

public class FilmObjectConverter {

    public static FilmRestView toRestView(Film film) {
        return new FilmRestView(film.getId(),
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getLikes());
    }

    public static Film toDomainObject(FilmRestCommand filmRestCommand) {
        Film film = Film.builder()
                .id(filmRestCommand.getId())
                .name(filmRestCommand.getName())
                .description(filmRestCommand.getDescription())
                .releaseDate(filmRestCommand.getReleaseDate())
                .duration(filmRestCommand.getDuration())
                .build();
        if (filmRestCommand.getLikes() != null && !filmRestCommand.getLikes().isEmpty()) {
            filmRestCommand.getLikes().forEach(whoLikedId -> film.getLikes().add(whoLikedId));
        }
        return film;
    }

}