package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import ru.yandex.practicum.filmorate.model.controllercommandclasses.restcommand.impl.FilmRestCommand;
import ru.yandex.practicum.filmorate.model.controllercommandclasses.restcommand.impl.UserRestCommand;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundInStorageException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryStorage;

@Service
@RequiredArgsConstructor
public class FilmServiceImpl implements FilmService {
    private final InMemoryStorage<Film> films;
    private final InMemoryStorage<User> users;

    @Override
    public List<UserRestCommand> addLikeToFilmLikesSet(long filmId, long userId)
            throws ObjectNotFoundInStorageException {
        Film film = films.getById(filmId);
        users.getById(userId);                                      // Для проверки, сохранен ли User с указанным id
        film.getLikes().add(userId);
        films.update(film);
        return film.getLikes().stream()
                .map(users::getById)
                .map(UserRestCommand::new)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserRestCommand> removeLikeFromFilmLikesSet(long filmId, long userId)
            throws ObjectNotFoundInStorageException {
        Film film = films.getById(filmId);
        users.getById(userId);
        film.getLikes().remove(userId);
        films.update(film);
        return film.getLikes().stream()
                .map(users::getById)
                .map(UserRestCommand::new)
                .collect(Collectors.toList());
    }

    @Override
    public List<FilmRestCommand> getMostLikedFilms(int count) {
        return films.getAll().stream()
                .sorted((film1, film2) -> film2.getLikes().size() - film1.getLikes().size())
                .limit(count)
                .map(FilmRestCommand::new)
                .collect(Collectors.toList());
    }

}