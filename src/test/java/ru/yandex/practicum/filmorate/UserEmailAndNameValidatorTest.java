package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;

import ru.yandex.practicum.filmorate.customvalidation.customvalidators.UserEmailAndNameValidator;
import ru.yandex.practicum.filmorate.exception.UserValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

public class UserEmailAndNameValidatorTest {
    private static User user = User.builder()
            .id(0)
            .email("sexmaster96@gmail.com")
            .login("tecktonick_killer")
            .name("Владимир")
            .birthday(LocalDate.of(1996, 12, 12))
            .build();

    @Test
    public void shouldCheckValidUserWithEmptyEmailListAndListWithoutDuplicates() {
        UserEmailAndNameValidator.checkUserBeforeSaving(user, Collections.emptyList());
        UserEmailAndNameValidator.checkUserBeforeSaving(user, List.of(
                User.builder().id(1).email("sexmaster97@gmail.com").login("tecktonick_killer").name("Владимир")
                        .birthday(LocalDate.of(1996, 12, 12)).build(),
                User.builder().id(2).email("sosiska").login("tecktonick_killer").name("Владимир")
                        .birthday(LocalDate.of(1996, 12, 12)).build(),
                User.builder().id(3).email("sugaronsand@yandex.ru").login("tecktonick_killer").name("Владимир")
                        .birthday(LocalDate.of(1996, 12, 12)).build()));
    }

    @Test
    public void shouldThrowExceptionWhenCheckWithListWithDuplicates() {
        assertThrows(UserValidationException.class, () -> UserEmailAndNameValidator.checkUserBeforeSaving(user, List.of(
                User.builder().id(1).email("sexmaster97@gmail.com").login("tecktonick_killer").name("Владимир")
                        .birthday(LocalDate.of(1996, 12, 12)).build(),
                User.builder().id(2).email("sexmaster96@gmail.com").login("tecktonick_killer").name("Владимир")
                        .birthday(LocalDate.of(1996, 12, 12)).build(),
                User.builder().id(3).email("sugaronsand@yandex.ru").login("tecktonick_killer").name("Владимир")
                        .birthday(LocalDate.of(1996, 12, 12)).build())));
    }

    @Test
    public void shouldReturnPreviousUserWhenCheckNotNullAndNotBlankName() {
        User checkedUser = UserEmailAndNameValidator.getUserWithCheckedName(user);
        assertEquals(user, checkedUser);
    }

    @Test
    public void shouldReturnValidUserWhenCheckNullOrBlankName() {
        User user1 = user.toBuilder().name("").build();
        User user2 = user.toBuilder().name(" ").build();
        User user3 = user.toBuilder().name("\n\r\t").build();
        User user4 = User.builder()
                .id(0)
                .email("sexmaster96@gmail.com")
                .login("tecktonick_killer")
                .birthday(LocalDate.of(1996, 12, 12))
                .build();
        user1 = UserEmailAndNameValidator.getUserWithCheckedName(user1);
        user2 = UserEmailAndNameValidator.getUserWithCheckedName(user2);
        user3 = UserEmailAndNameValidator.getUserWithCheckedName(user3);
        user4 = UserEmailAndNameValidator.getUserWithCheckedName(user4);
        user = user.toBuilder().name(user.getLogin()).build();
        assertEquals(user, user1);
        assertEquals(user, user2);
        assertEquals(user, user3);
        assertEquals(user, user4);
    }

}