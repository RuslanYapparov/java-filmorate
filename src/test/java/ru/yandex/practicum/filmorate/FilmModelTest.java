package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.time.LocalDate;

import ru.yandex.practicum.filmorate.model.FilmModel;

public class FilmModelTest {

    @Test
    public void shouldBeCreatedWithValidFieldsBeEqualToItsCloneAndHaveEqualHashCode() {
        FilmModel filmModel1 = FilmModel.builder()
                .id(0)
                .name("Whores & whales")
                .description("Adventures of women in whales world")
                .releaseDate(LocalDate.of(1996, 12, 12))
                .duration(127)
                .build();
        assertNotNull(filmModel1);
        FilmModel filmModel2 = FilmModel.builder()
                .id(0)
                .name("Whores & whales")
                .description("Adventures of women in whales world")
                .releaseDate(LocalDate.of(1996, 12, 12))
                .duration(127)
                .build();
        assertEquals(filmModel1, filmModel2);
        assertEquals(filmModel1.hashCode(), filmModel2.hashCode());
    }

    @Test
    public void shouldBeNotEqualToItsCloneWithSomeDifferencesAndHaveNotSameHashCode() {
        FilmModel filmModel1 = FilmModel.builder()
                .id(0)
                .name("Whores & whales")
                .description("Adventures of women in whales world")
                .releaseDate(LocalDate.of(1996, 12, 12))
                .duration(127)
                .build();
        FilmModel filmModel2 = filmModel1.toBuilder()
                .id(1)
                .build();
        FilmModel filmModel3 = filmModel1.toBuilder()
                .name("Whores & holes")
                .build();
        FilmModel filmModel4 = filmModel1.toBuilder()
                .description("Adventures of women in video world")
                .build();
        FilmModel filmModel5 = filmModel1.toBuilder()
                .duration(125)
                .build();
        FilmModel filmModel6 = filmModel1.toBuilder()
                .releaseDate(LocalDate.of(1996, 12, 13))
                .build();
        assertNotEquals(filmModel1, filmModel2);
        assertNotEquals(filmModel1.hashCode(), filmModel2.hashCode());
        assertNotEquals(filmModel1, filmModel3);
        assertNotEquals(filmModel1.hashCode(), filmModel3.hashCode());
        assertNotEquals(filmModel1, filmModel4);
        assertNotEquals(filmModel1.hashCode(), filmModel4.hashCode());
        assertNotEquals(filmModel1, filmModel5);
        assertNotEquals(filmModel1.hashCode(), filmModel5.hashCode());
        assertNotEquals(filmModel1, filmModel6);
        assertNotEquals(filmModel1.hashCode(), filmModel6.hashCode());
    }

    @Test
    public void shouldCorrectToStringResult() {
        FilmModel filmModel1 = FilmModel.builder()
                .id(0)
                .name("Whores & whales")
                .description("Adventures of women in whales world")
                .releaseDate(LocalDate.of(1996, 12, 12))
                .duration(127)
                .build();
        assertEquals("FilmModel(id=0, name=Whores & whales, description=Adventures of women in whales world, " +
                "releaseDate=1996-12-12, duration=127, likes=[])", filmModel1.toString());
    }

}