package ru.yandex.practicum.filmorate.custom_validation.custom_annotations;

import ru.yandex.practicum.filmorate.custom_validation.custom_validators.SpacesInStringValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = SpacesInStringValidator.class)
public @interface WithoutSpaces {
    String message() default "Не допускается использование пробелов в строковом значении данного поля";
    Class<?>[] groups() default { };
    Class<? extends Payload>[] payload() default { };

}