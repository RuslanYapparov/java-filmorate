package ru.yandex.practicum.filmorate.customvalidation.customvalidators;

import ru.yandex.practicum.filmorate.customvalidation.customannotations.NotImmortal;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;

public class BirthdayValidator implements ConstraintValidator<NotImmortal, LocalDate> {
    private static final LocalDate BOUNDARY_DATE_FOR_AGE = LocalDate.of(1900, 1, 1);

    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
        return value != null && value.isAfter(BOUNDARY_DATE_FOR_AGE);
    }

}