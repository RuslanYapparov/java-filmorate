package ru.yandex.practicum.filmorate.customvalidation.customannotations;

import ru.yandex.practicum.filmorate.customvalidation.customvalidators.SpacesInStringValidator;

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
    // Можно было использовать аннотацию @Pattern(regexp = ...), но она показалась мне менее читаемой
    String message() default "Не допускается использование пробелов в строковом значении данного поля";
    Class<?>[] groups() default { };
    Class<? extends Payload>[] payload() default { };

}