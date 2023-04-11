package ru.yandex.practicum.filmorate.model.restinteractionmodel.restview;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@lombok.Data
public class ErrorResponseView {
    private final int statusCode;
    private final String exception;
    private final String debugMessage;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<String> errors;

}