package ru.yandex.practicum.filmorate.model.service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Review {
    Integer reviewId;
    String content;
    Boolean isPositive;
    Long userId;
    Long filmId;
    Long useful;
}