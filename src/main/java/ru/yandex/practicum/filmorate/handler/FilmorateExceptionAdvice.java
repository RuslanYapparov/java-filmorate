package ru.yandex.practicum.filmorate.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.yandex.practicum.filmorate.exception.StorageManagementException;
import ru.yandex.practicum.filmorate.exception.UserValidationException;

@ControllerAdvice
public class FilmorateExceptionAdvice {

    @ExceptionHandler(StorageManagementException.class)
    public ResponseEntity<String> handleStorageException(StorageManagementException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UserValidationException.class)
    public ResponseEntity<String> handleUserValidationException(UserValidationException exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }

}
