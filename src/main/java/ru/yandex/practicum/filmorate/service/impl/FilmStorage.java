package ru.yandex.practicum.filmorate.service.impl;

import org.springframework.stereotype.Service;

import ru.yandex.practicum.filmorate.model.Film;

@Service
public class FilmStorage extends InMemoryStorageImpl<Film> {
}
