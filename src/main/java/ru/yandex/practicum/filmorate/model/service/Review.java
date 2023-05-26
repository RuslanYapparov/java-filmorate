package ru.yandex.practicum.filmorate.model.service;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Review {
    long reviewId;
    String content;
    boolean isPositive;
    long userId;
    long filmId;
    int useful;

    public boolean getIsPositive() {  // Пришлось реализовать метод-геттер для boolean-поля с неправильными (согласно
        return isPositive; // принятым и описанным в теории правилам), потому что он необходим для мэппинга в mapstruct
    }

}