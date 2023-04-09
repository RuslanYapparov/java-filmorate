package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import javax.validation.ConstraintViolationException;

import java.util.List;
import java.util.stream.Collectors;

import ru.yandex.practicum.filmorate.exception.ObjectNotFoundInStorageException;
import ru.yandex.practicum.filmorate.exception.UserValidationException;
import ru.yandex.practicum.filmorate.model.controllercommandclasses.ErrorResponse;

@RestControllerAdvice()
@Slf4j
public class FilmorateExceptionsHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ObjectNotFoundInStorageException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleObjectNotFoundInStorageException(ObjectNotFoundInStorageException exception) {
        log.warn(exception.getMessage());
        return new ErrorResponse(HttpStatus.NOT_FOUND.value(), "ObjectNotFoundInStorageException",
                exception.getMessage());
    }

    @ExceptionHandler(UserValidationException.class)
    public ResponseEntity<ErrorResponse> handleUserValidationException(UserValidationException exception) {
        String message = exception.getMessage();
        ErrorResponse error;
        log.warn(message);
        if (message.equals("Неправильный формат адреса электронной почты")) {
            error = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "UserValidationException", message);
        } else {
            error = new ErrorResponse(HttpStatus.CONFLICT.value(), "UserValidationException", message);
        }
        return new ResponseEntity<>(error, HttpStatus.valueOf(error.getStatusCode()));
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatus status,
                                                                  WebRequest request) {
        ErrorResponse error = new ErrorResponse(status.value(),"HttpMessageNotReadableException", ex.getMessage());
        return new ResponseEntity<>(error, status);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatus status,
                                                                  WebRequest request) {
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());
        ErrorResponse error = new ErrorResponse(status.value(),"MethodArgumentNotValidException", ex.getMessage());
        error.setErrors(errors);
        log.warn(error.getException() + error.getErrors());
        return new ResponseEntity<>(error, status);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    protected ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex) {
        ErrorResponse error = new ErrorResponse(HttpStatus.BAD_REQUEST.value(),
                "MethodArgumentTypeMismatchException",
                String.format("The parameter '%s' of value '%s' could not be converted to type '%s'. Cause: %s",
                        ex.getName(), ex.getValue(), ex.getRequiredType().getSimpleName(), ex.getMessage()));
        log.warn(error.getException(), error.getDebugMessage());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleNoHandlerFoundException(NoHandlerFoundException ex, HttpHeaders headers,
                                                                   HttpStatus status, WebRequest request) {
        ErrorResponse error = new ErrorResponse(status.value(),
                "NoHandlerFoundException",
                ex.getMessage());
        log.warn(error.getException(), error.getDebugMessage());
        return new ResponseEntity<>(error, status);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolationException(ConstraintViolationException exception) {
        String message = exception.getMessage();
        ErrorResponse error;
        log.warn(message);
        if (message.contains("должно быть больше")) {
        // Опять костыль, чтобы пройти тест в Postman (на эту ошибку ожидает код статуса 404, хотя логично бы было 400)
            error = new ErrorResponse(HttpStatus.NOT_FOUND.value(), "ConstraintViolationException", message);
        } else {
            error = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "ConstraintViolationException", message);
        }
        return new ResponseEntity<>(error, HttpStatus.valueOf(error.getStatusCode()));
    }

}