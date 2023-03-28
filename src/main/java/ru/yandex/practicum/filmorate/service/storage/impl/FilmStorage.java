package ru.yandex.practicum.filmorate.service.storage.impl;

import org.springframework.stereotype.Service;

import ru.yandex.practicum.filmorate.model.Film;

@Service
public class FilmStorage extends InMemoryStorageImpl<Film> {
}
