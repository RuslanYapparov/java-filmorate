package ru.yandex.practicum.filmorate.service.in_memory;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import ru.yandex.practicum.filmorate.exception.ObjectNotFoundInStorageException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.service.Film;
import ru.yandex.practicum.filmorate.model.service.User;
import ru.yandex.practicum.filmorate.model.presentation.rest_view.FilmRestView;
import ru.yandex.practicum.filmorate.model.presentation.rest_view.UserRestView;
import ru.yandex.practicum.filmorate.storage.InMemoryStorage;

@Service
@RequiredArgsConstructor
public class InMemoryFilmServiceImpl implements InMemoryFilmService {
    private final InMemoryStorage<Film> films;
    private final InMemoryStorage<User> users;
    private final FilmMapper filmMapper;
    private final UserMapper userMapper;

    @Override
    public List<UserRestView> addLikeToFilmLikesSet(long filmId, long userId)
            throws ObjectNotFoundInStorageException {
        Film film = films.getById(filmId);
        users.getById(userId);                                      // Для проверки, сохранен ли User с указанным id
        film.getMarksFrom().add(userId);
        films.update(film);
        return film.getMarksFrom().stream()
                .map(users::getById)
                .map(userMapper::toRestView)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserRestView> removeLikeFromFilmLikesSet(long filmId, long userId)
            throws ObjectNotFoundInStorageException {
        Film film = films.getById(filmId);
        users.getById(userId);
        film.getMarksFrom().remove(userId);
        films.update(film);
        return film.getMarksFrom().stream()
                .map(users::getById)
                .map(userMapper::toRestView)
                .collect(Collectors.toList());
    }

    @Override
    public List<FilmRestView> getMostLikedFilms(int count) {
        return films.getAll().stream()
                .sorted(Comparator.comparingDouble(film -> 10.0 - (double) film.getRate()))
                .limit(count)
                .map(filmMapper::toRestView)
                .collect(Collectors.toList());
    }

}