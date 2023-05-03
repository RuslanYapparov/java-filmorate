package ru.yandex.practicum.filmorate.service.inmemory;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import ru.yandex.practicum.filmorate.exception.ObjectNotFoundInStorageException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.domain.Film;
import ru.yandex.practicum.filmorate.model.domain.User;
import ru.yandex.practicum.filmorate.model.presentation.restview.FilmRestView;
import ru.yandex.practicum.filmorate.model.presentation.restview.UserRestView;
import ru.yandex.practicum.filmorate.storage.InMemoryStorage;

@Service
@Qualifier("inMemoryFilmService")
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
        film.getLikes().add(userId);
        films.update(film);
        return film.getLikes().stream()
                .map(users::getById)
                .map(userMapper::toRestView)
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
                .map(userMapper::toRestView)
                .collect(Collectors.toList());
    }

    @Override
    public List<FilmRestView> getMostLikedFilms(int count) {
        return films.getAll().stream()
                .sorted((film1, film2) -> film2.getLikes().size() - film1.getLikes().size())
                .limit(count)
                .map(filmMapper::toRestView)
                .collect(Collectors.toList());
    }

}