package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;

import ru.yandex.practicum.filmorate.customvalidation.customvalidators.UserEmailAndNameValidator;
import ru.yandex.practicum.filmorate.exception.UserValidationException;
import ru.yandex.practicum.filmorate.model.UserModel;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

public class UserlEmailAndNameValidatorTest {
    private static UserModel userModel = UserModel.builder()
            .id(0)
            .email("sexmaster96@gmail.com")
            .login("tecktonick_killer")
            .name("Владимир")
            .birthday(LocalDate.of(1996, 12, 12))
            .build();

    @Test
    public void shouldCheckValidUserWithEmptyEmailListAndListWithoutDuplicates() {
        UserEmailAndNameValidator.checkUserBeforeSaving(userModel, Collections.emptyList());
        UserEmailAndNameValidator.checkUserBeforeSaving(userModel, List.of(
                UserModel.builder().id(1).email("sexmaster97@gmail.com").login("tecktonick_killer").name("Владимир")
                        .birthday(LocalDate.of(1996, 12, 12)).build(),
                UserModel.builder().id(2).email("sosiska").login("tecktonick_killer").name("Владимир")
                        .birthday(LocalDate.of(1996, 12, 12)).build(),
                UserModel.builder().id(3).email("sugaronsand@yandex.ru").login("tecktonick_killer").name("Владимир")
                        .birthday(LocalDate.of(1996, 12, 12)).build()));
    }

    @Test
    public void shouldThrowExceptionWhenCheckWithListWithDuplicates() {
        assertThrows(UserValidationException.class, () -> UserEmailAndNameValidator.checkUserBeforeSaving(userModel, List.of(
                UserModel.builder().id(1).email("sexmaster97@gmail.com").login("tecktonick_killer").name("Владимир")
                        .birthday(LocalDate.of(1996, 12, 12)).build(),
                UserModel.builder().id(2).email("sexmaster96@gmail.com").login("tecktonick_killer").name("Владимир")
                        .birthday(LocalDate.of(1996, 12, 12)).build(),
                UserModel.builder().id(3).email("sugaronsand@yandex.ru").login("tecktonick_killer").name("Владимир")
                        .birthday(LocalDate.of(1996, 12, 12)).build())));
    }

    @Test
    public void shouldReturnPreviousUserWhenCheckNotNullAndNotBlankName() {
        UserModel checkedUserModel = UserEmailAndNameValidator.getUserWithCheckedName(userModel);
        assertEquals(userModel, checkedUserModel);
    }

    @Test
    public void shouldReturnValidUserWhenCheckNullOrBlankName() {
        UserModel userModel1 = userModel.toBuilder().name("").build();
        UserModel userModel2 = userModel.toBuilder().name(" ").build();
        UserModel userModel3 = userModel.toBuilder().name("\n\r\t").build();
        UserModel userModel4 = UserModel.builder()
                .id(0)
                .email("sexmaster96@gmail.com")
                .login("tecktonick_killer")
                .birthday(LocalDate.of(1996, 12, 12))
                .build();
        userModel1 = UserEmailAndNameValidator.getUserWithCheckedName(userModel1);
        userModel2 = UserEmailAndNameValidator.getUserWithCheckedName(userModel2);
        userModel3 = UserEmailAndNameValidator.getUserWithCheckedName(userModel3);
        userModel4 = UserEmailAndNameValidator.getUserWithCheckedName(userModel4);
        userModel = userModel.toBuilder().name(userModel.getLogin()).build();
        assertEquals(userModel, userModel1);
        assertEquals(userModel, userModel2);
        assertEquals(userModel, userModel3);
        assertEquals(userModel, userModel4);
    }

}