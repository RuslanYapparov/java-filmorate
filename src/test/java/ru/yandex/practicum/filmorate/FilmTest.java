package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.time.LocalDate;

import ru.yandex.practicum.filmorate.model.service.Film;

public class FilmTest {

    @Test
    public void shouldBeCreatedWithValidFieldsBeEqualToItsCloneAndHaveEqualHashCode() {
        Film film1 = Film.builder()
                .id(0)
                .name("Whores & whales")
                .description("Adventures of women in whales world")
                .releaseDate(LocalDate.of(1996, 12, 12))
                .duration(127)
                .build();
        assertNotNull(film1);
        Film film2 = Film.builder()
                .id(0)
                .name("Whores & whales")
                .description("Adventures of women in whales world")
                .releaseDate(LocalDate.of(1996, 12, 12))
                .duration(127)
                .build();
        assertEquals(film1, film2);
        assertEquals(film1.hashCode(), film2.hashCode());
    }

    @Test
    public void shouldBeNotEqualToItsCloneWithSomeDifferencesAndHaveNotSameHashCode() {
        Film film1 = Film.builder()
                .id(0)
                .name("Whores & whales")
                .description("Adventures of women in whales world")
                .releaseDate(LocalDate.of(1996, 12, 12))
                .duration(127)
                .build();
        Film film2 = film1.toBuilder()
                .id(1)
                .build();
        Film film3 = film1.toBuilder()
                .name("Whores & holes")
                .build();
        Film film4 = film1.toBuilder()
                .description("Adventures of women in video world")
                .build();
        Film film5 = film1.toBuilder()
                .duration(125)
                .build();
        Film film6 = film1.toBuilder()
                .releaseDate(LocalDate.of(1996, 12, 13))
                .build();
        assertNotEquals(film1, film2);
        assertNotEquals(film1.hashCode(), film2.hashCode());
        assertNotEquals(film1, film3);
        assertNotEquals(film1.hashCode(), film3.hashCode());
        assertNotEquals(film1, film4);
        assertNotEquals(film1.hashCode(), film4.hashCode());
        assertNotEquals(film1, film5);
        assertNotEquals(film1.hashCode(), film5.hashCode());
        assertNotEquals(film1, film6);
        assertNotEquals(film1.hashCode(), film6.hashCode());
    }

    @Test
    public void shouldCorrectToStringResult() {
        Film film1 = Film.builder()
                .id(0)
                .name("Whores & whales")
                .description("Adventures of women in whales world")
                .releaseDate(LocalDate.of(1996, 12, 12))
                .duration(127)
                .build();
        assertEquals("Film(id=0, name=Whores & whales, description=Adventures of women in whales world, " +
           "releaseDate=1996-12-12, duration=127, rate=0, rating=null, likes=null, genres=null)", film1.toString());
    }

}