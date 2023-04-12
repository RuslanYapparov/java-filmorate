package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.time.LocalDate;

import ru.yandex.practicum.filmorate.model.UserModel;

public class UserModelTest {

    @Test
    public void shouldBeCreatedWithValidFieldsBeEqualToItsCloneAndHaveEqualHashCode() {
        UserModel userModel1 = UserModel.builder()
                .id(0)
                .email("sexmaster96@gmail.com")
                .login("tecktonick_killer")
                .name("Владимир")
                .birthday(LocalDate.of(1996, 12, 12))
                .build();
        assertNotNull(userModel1);
        UserModel userModel2 = UserModel.builder()
                .id(0)
                .email("sexmaster96@gmail.com")
                .login("tecktonick_killer")
                .name("Владимир")
                .birthday(LocalDate.of(1996, 12, 12))
                .build();
        assertEquals(userModel1, userModel2);
        assertEquals(userModel1.hashCode(), userModel2.hashCode());
    }

    @Test
    public void shouldBeNotEqualToItsCloneWithSomeDifferencesAndHaveNotSameHashCode() {
        UserModel userModel1 = UserModel.builder()
                .id(0)
                .email("sexmaster96@gmail.com")
                .login("tecktonick_killer")
                .name("Владимир")
                .birthday(LocalDate.of(1996, 12, 12))
                .build();
        UserModel userModel2 = userModel1.toBuilder()
                .id(1)
                .build();
        UserModel userModel3 = userModel1.toBuilder()
                .email("sexmaster97@gmail.com")
                .build();
        UserModel userModel4 = userModel1.toBuilder()
                .login("tecktonick_kisser")
                .build();
        UserModel userModel5 = userModel1.toBuilder()
                .name("Владомир")
                .build();
        UserModel userModel6 = userModel1.toBuilder()
                .birthday(LocalDate.of(1996, 12, 13))
                .build();
        assertNotEquals(userModel1, userModel2);
        assertNotEquals(userModel1.hashCode(), userModel2.hashCode());
        assertNotEquals(userModel1, userModel3);
        assertNotEquals(userModel1.hashCode(), userModel3.hashCode());
        assertNotEquals(userModel1, userModel4);
        assertNotEquals(userModel1.hashCode(), userModel4.hashCode());
        assertNotEquals(userModel1, userModel5);
        assertNotEquals(userModel1.hashCode(), userModel5.hashCode());
        assertNotEquals(userModel1, userModel6);
        assertNotEquals(userModel1.hashCode(), userModel6.hashCode());
    }

    @Test
    public void shouldHaveDefiniteToStringResult() {
        UserModel userModel1 = UserModel.builder()
                .id(0)
                .email("sexmaster96@gmail.com")
                .login("tecktonick_killer")
                .name("Владимир")
                .birthday(LocalDate.of(1996, 12, 12))
                .build();
        assertEquals("UserModel(id=0, email=sexmaster96@gmail.com, login=tecktonick_killer, name=Владимир, " +
                "birthday=1996-12-12, friends=[])", userModel1.toString());
    }

}