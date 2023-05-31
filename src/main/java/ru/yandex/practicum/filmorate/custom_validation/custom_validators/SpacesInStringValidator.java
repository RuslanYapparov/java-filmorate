package ru.yandex.practicum.filmorate.custom_validation.custom_validators;

import ru.yandex.practicum.filmorate.custom_validation.custom_annotations.WithoutSpaces;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class SpacesInStringValidator implements ConstraintValidator<WithoutSpaces, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value != null && !value.contains(" ");
    }

}