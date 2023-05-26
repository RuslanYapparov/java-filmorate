package ru.yandex.practicum.filmorate.exception;

public class BadRequestBodyException extends RuntimeException {

    public BadRequestBodyException(String message) {
        super(message);
    }

}