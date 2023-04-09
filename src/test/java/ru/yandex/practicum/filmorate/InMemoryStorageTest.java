package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ru.yandex.practicum.filmorate.exception.ObjectNotFoundInStorageException;
import ru.yandex.practicum.filmorate.storage.InMemoryStorage;
import ru.yandex.practicum.filmorate.storage.impl.InMemoryStorageImpl;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryStorageTest {
    private static InMemoryStorage<String> storageForTest;

    @BeforeAll
    public static void initialize() {
        storageForTest = new InMemoryStorageImpl<>();
        assertNotNull(storageForTest);
        assertEquals(1, storageForTest.produceId());
        assertEquals(0, storageForTest.getSize());
        assertEquals(new ArrayList<>(), storageForTest.getAll());
        assertThrows(ObjectNotFoundInStorageException.class, () -> storageForTest.getById(1));
        assertThrows(ObjectNotFoundInStorageException.class, () -> storageForTest.deleteById(1));
    }

    @BeforeEach
    public void createNewStorage() {
        storageForTest = new InMemoryStorageImpl<>();
    }

    @AfterEach
    public void shouldBeEmptyAfterTest() {
        storageForTest.deleteAll();
        assertEquals(0, storageForTest.getSize());
    }

    @Test
    public void shouldSaveUpdateAndDeleteObject() {
        storageForTest.save("Java");
        storageForTest.save("Coffee");
        assertEquals(2, storageForTest.getSize());
        assertEquals("Java", storageForTest.getById(1));
        storageForTest.update("Coffee");
        assertEquals(2, storageForTest.getSize());
        assertEquals("Coffee", storageForTest.getById(2));
        assertEquals("Coffee", storageForTest.deleteById(2));
        assertEquals(1, storageForTest.getSize());
    }

    @Test
    public void shouldThrowExceptionWhenTryingToGetSomethingWithIncorrectId() {
        assertEquals("Java", storageForTest.save("Java"));
        assertEquals(1, storageForTest.getSize());
        assertEquals("Java", storageForTest.getById(1));
        assertThrows(ObjectNotFoundInStorageException.class, () -> storageForTest.getById(777));
        assertThrows(ObjectNotFoundInStorageException.class, () -> storageForTest.deleteById(777));
    }

}