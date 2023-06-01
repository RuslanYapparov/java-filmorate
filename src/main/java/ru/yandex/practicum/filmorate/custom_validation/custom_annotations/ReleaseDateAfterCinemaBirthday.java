package ru.yandex.practicum.filmorate.custom_validation.custom_annotations;

import ru.yandex.practicum.filmorate.custom_validation.custom_validators.ReleaseDateValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ReleaseDateValidator.class)
public @interface ReleaseDateAfterCinemaBirthday {

    String message() default "Не указана дата релиза фильма либо введённая дата раньше Дня рождения кино (28.12.1895)";
    Class<?>[] groups() default { };
    Class<? extends Payload>[] payload() default { };

}