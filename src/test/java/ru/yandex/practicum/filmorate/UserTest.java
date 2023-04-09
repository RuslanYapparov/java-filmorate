package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.time.LocalDate;

import ru.yandex.practicum.filmorate.model.User;

public class UserTest {

    @Test
    public void shouldBeCreatedWithValidFieldsBeEqualToItsCloneAndHaveEqualHashCode() {
        User user1 = User.builder()
                .id(0)
                .email("sexmaster96@gmail.com")
                .login("tecktonick_killer")
                .name("Владимир")
                .birthday(LocalDate.of(1996, 12, 12))
                .build();
        assertNotNull(user1);
        User user2 = User.builder()
                .id(0)
                .email("sexmaster96@gmail.com")
                .login("tecktonick_killer")
                .name("Владимир")
                .birthday(LocalDate.of(1996, 12, 12))
                .build();
        assertEquals(user1, user2);
        assertEquals(user1.hashCode(), user2.hashCode());
    }

    @Test
    public void shouldBeNotEqualToItsCloneWithSomeDifferencesAndHaveNotSameHashCode() {
        User user1 = User.builder()
                .id(0)
                .email("sexmaster96@gmail.com")
                .login("tecktonick_killer")
                .name("Владимир")
                .birthday(LocalDate.of(1996, 12, 12))
                .build();
        User user2 = user1.toBuilder()
                .id(1)
                .build();
        User user3 = user1.toBuilder()
                .email("sexmaster97@gmail.com")
                .build();
        User user4 = user1.toBuilder()
                .login("tecktonick_kisser")
                .build();
        User user5 = user1.toBuilder()
                .name("Владомир")
                .build();
        User user6 = user1.toBuilder()
                .birthday(LocalDate.of(1996, 12, 13))
                .build();
        assertNotEquals(user1, user2);
        assertNotEquals(user1.hashCode(), user2.hashCode());
        assertNotEquals(user1, user3);
        assertNotEquals(user1.hashCode(), user3.hashCode());
        assertNotEquals(user1, user4);
        assertNotEquals(user1.hashCode(), user4.hashCode());
        assertNotEquals(user1, user5);
        assertNotEquals(user1.hashCode(), user5.hashCode());
        assertNotEquals(user1, user6);
        assertNotEquals(user1.hashCode(), user6.hashCode());
    }

    @Test
    public void shouldHaveDefiniteToStringResult() {
        User user1 = User.builder()
                .id(0)
                .email("sexmaster96@gmail.com")
                .login("tecktonick_killer")
                .name("Владимир")
                .birthday(LocalDate.of(1996, 12, 12))
                .build();
        assertEquals("User(id=0, email=sexmaster96@gmail.com, login=tecktonick_killer, name=Владимир, " +
                "birthday=1996-12-12, friends=[])", user1.toString());
    }

}