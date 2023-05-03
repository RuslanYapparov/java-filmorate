package ru.yandex.practicum.filmorate.model.domain;

import ru.yandex.practicum.filmorate.exception.ObjectNotFoundInStorageException;

@lombok.AllArgsConstructor
@lombok.Getter
public enum RatingMpa {
    G(1, "G"),
    PG(2, "PG"),
    PG_13(3, "PG-13"),
    R(4, "R"),
    NC_17(5, "NC-17");

    private final int id;
    private final String name;


    public static RatingMpa getRatingById(int id) {
        switch (id) {
            case 1:
                return G;
            case 2:
                return PG;
            case 3:
                return PG_13;
            case 4:
                return R;
            case 5:
                return NC_17;
            default:
                throw new ObjectNotFoundInStorageException("Невозможно найти значение рейтинга с указанным " +
                        "идентификатором");
        }
    }

}