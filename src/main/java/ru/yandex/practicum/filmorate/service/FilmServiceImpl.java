package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import ru.yandex.practicum.filmorate.exception.ObjectNotFoundInStorageException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.FilmModel;
import ru.yandex.practicum.filmorate.model.UserModel;
import ru.yandex.practicum.filmorate.model.dto.restview.FilmRestView;
import ru.yandex.practicum.filmorate.model.dto.restview.UserRestView;
import ru.yandex.practicum.filmorate.storage.InMemoryStorage;

@Service
@RequiredArgsConstructor
public class FilmServiceImpl implements FilmService {
    private final InMemoryStorage<FilmModel> films;
    private final InMemoryStorage<UserModel> users;
    private final FilmMapper filmMapper;
    private final UserMapper userMapper;

    @Override
    public List<UserRestView> addLikeToFilmLikesSet(long filmId, long userId)
            throws ObjectNotFoundInStorageException {
        FilmModel filmModel = films.getById(filmId);
        users.getById(userId);                                      // Для проверки, сохранен ли User с указанным id
        filmModel.getLikes().add(userId);
        films.update(filmModel);
        return filmModel.getLikes().stream()
                .map(users::getById)
                .map(userMapper::toRestView)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserRestView> removeLikeFromFilmLikesSet(long filmId, long userId)
            throws ObjectNotFoundInStorageException {
        FilmModel filmModel = films.getById(filmId);
        users.getById(userId);
        filmModel.getLikes().remove(userId);
        films.update(filmModel);
        return filmModel.getLikes().stream()
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