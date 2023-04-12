package ru.yandex.practicum.filmorate.exception;

public class ObjectNotFoundInStorageException extends RuntimeException {

    public ObjectNotFoundInStorageException(String message) {
        super(message);
    }

}