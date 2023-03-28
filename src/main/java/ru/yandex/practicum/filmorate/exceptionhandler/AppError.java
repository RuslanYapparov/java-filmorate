package ru.yandex.practicum.filmorate.exceptionhandler;

@lombok.Data
public class AppError {
    private final int statusCode;
    private final String message;

}