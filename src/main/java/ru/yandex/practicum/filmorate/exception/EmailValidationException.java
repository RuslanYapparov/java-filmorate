package ru.yandex.practicum.filmorate.exception;

public class EmailValidationException extends RuntimeException {

    public EmailValidationException(String message) {
        super(message);
    }

}