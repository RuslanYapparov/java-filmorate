package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import ru.yandex.practicum.filmorate.exception.ObjectNotFoundInStorageException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.restinteractionmodel.restview.FilmRestView;
import ru.yandex.practicum.filmorate.model.restinteractionmodel.restview.UserRestView;
import ru.yandex.practicum.filmorate.storage.InMemoryStorage;
import ru.yandex.practicum.filmorate.util.FilmObjectConverter;
import ru.yandex.practicum.filmorate.util.UserObjectConverter;

@Service
@RequiredArgsConstructor
public class FilmServiceImpl implements FilmService {
    private final InMemoryStorage<Film> films;
    private final InMemoryStorage<User> users;

    @Override
    public List<UserRestView> addLikeToFilmLikesSet(long filmId, long userId)
            throws ObjectNotFoundInStorageException {
        Film film = films.getById(filmId);
        users.getById(userId);                                      // Для проверки, сохранен ли User с указанным id
        film.getLikes().add(userId);
        films.update(film);
        return film.getLikes().stream()
                .map(users::getById)
                .map(UserObjectConverter::toRestView)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserRestView> removeLikeFromFilmLikesSet(long filmId, long userId)
            throws ObjectNotFoundInStorageException {
        Film film = films.getById(filmId);
        users.getById(userId);
        film.getLikes().remove(userId);
        films.update(film);
        return film.getLikes().stream()
                .map(users::getById)
                .map(UserObjectConverter::toRestView)
                .collect(Collectors.toList());
    }

    @Override
    public List<FilmRestView> getMostLikedFilms(int count) {
        return films.getAll().stream()
                .sorted((film1, film2) -> film2.getLikes().size() - film1.getLikes().size())
                .limit(count)
                .map(FilmObjectConverter::toRestView)
                .collect(Collectors.toList());
    }

}