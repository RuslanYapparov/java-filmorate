package ru.yandex.practicum.filmorate.model.domain;

import ru.yandex.practicum.filmorate.exception.ObjectNotFoundInStorageException;

@lombok.AllArgsConstructor
@lombok.Getter
public enum Genre {
    COMEDY (1, "Комедия"),
    DRAMA (2, "Драма"),
    CARTOON (3, "Мультфильм"),
    THRILLER (4, "Триллер"),
    DOCUMENTARY (5, "Документальный"),
    ACTION (6, "Боевик");

    private final int id;
    private final String byRus;

    public static Genre getGenreById(int id) {
        switch (id) {
            case 1:
                return COMEDY;
            case 2:
                return DRAMA;
            case 3:
                return CARTOON;
            case 4:
                return THRILLER;
            case 5:
                return DOCUMENTARY;
            case 6:
                return ACTION;
            default:
                throw new ObjectNotFoundInStorageException("Невозможно найти значение жанра с указанным " +
                        "идентификатором");
        }
    }

}