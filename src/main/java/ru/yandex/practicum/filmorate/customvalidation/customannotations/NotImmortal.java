package ru.yandex.practicum.filmorate.customvalidation.customannotations;

import ru.yandex.practicum.filmorate.customvalidation.customvalidators.BirthdayValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = BirthdayValidator.class)
public @interface NotImmortal {

    String message() default "Не указан день рождения либо введена некорректная дата рождения. В случае, если ошибки " +
            "нет пользователю следует обратиться в за консультацией в Службу учета и контроля бессмертных граждан";
    Class<?>[] groups() default { };
    Class<? extends Payload>[] payload() default { };

}