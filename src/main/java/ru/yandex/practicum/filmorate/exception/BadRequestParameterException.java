package ru.yandex.practicum.filmorate.exception;

public class BadRequestParameterException extends RuntimeException {

    public BadRequestParameterException(String message) {
        super(message);
    }

}