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

    public static Film toDomainObject(FilmRestCommand restFilm) {
        Film film = Film.builder()
                .id(restFilm.getId())
                .name(restFilm.getName())
                .description(restFilm.getDescription())
                .releaseDate(restFilm.getReleaseDate())
                .duration(restFilm.getDuration())
                .build();
        if (restFilm.getLikes() != null && !restFilm.getLikes().isEmpty()) {
            restFilm.getLikes().forEach(restId -> film.getLikes().add(restId));
        }
        return film;
    }

}