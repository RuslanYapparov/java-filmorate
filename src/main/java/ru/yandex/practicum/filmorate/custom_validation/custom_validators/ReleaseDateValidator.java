package ru.yandex.practicum.filmorate.custom_validation.custom_validators;

import ru.yandex.practicum.filmorate.custom_validation.custom_annotations.ReleaseDateAfterCinemaBirthday;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;

public class ReleaseDateValidator implements ConstraintValidator<ReleaseDateAfterCinemaBirthday, LocalDate> {
    private static final LocalDate EARLIEST_RELEASE_DATE = LocalDate.of(1895, 12, 28);

    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
        return value != null && value.isAfter(EARLIEST_RELEASE_DATE);
    }

}