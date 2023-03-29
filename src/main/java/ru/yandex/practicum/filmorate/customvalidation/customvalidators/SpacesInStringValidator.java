package ru.yandex.practicum.filmorate.customvalidation.customvalidators;

import ru.yandex.practicum.filmorate.customvalidation.customannotations.WithoutSpaces;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class SpacesInStringValidator implements ConstraintValidator<WithoutSpaces, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value != null && !value.contains(" ");
    }

}