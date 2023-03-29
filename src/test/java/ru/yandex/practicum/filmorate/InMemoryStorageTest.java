package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import ru.yandex.practicum.filmorate.exception.StorageManagementException;
import ru.yandex.practicum.filmorate.service.storage.InMemoryStorage;
import ru.yandex.practicum.filmorate.service.storage.impl.InMemoryStorageImpl;

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
        assertThrows(StorageManagementException.class, () -> storageForTest.getById(1));
        assertThrows(StorageManagementException.class, () -> storageForTest.update(1, "Milk"));
        assertThrows(StorageManagementException.class, () -> storageForTest.deleteById(1));
    }

    @AfterEach
    public void shouldBeEmptyAfterTest() {
        storageForTest.deleteAll();
        assertEquals(0, storageForTest.getSize());
    }

    @Test
    public void shouldSaveUpdateAndDeleteObjectWithRandomIntId() {
        storageForTest.save(777, "Java");
        storageForTest.save(77, "Coffee");
        assertEquals(2, storageForTest.getSize());
        assertEquals("Java", storageForTest.getById(777));
        storageForTest.update(777, "Coffee");
        assertEquals(2, storageForTest.getSize());
        assertEquals("Coffee", storageForTest.getById(777));
        assertEquals("Coffee", storageForTest.deleteById(777));
        assertEquals(1, storageForTest.getSize());
    }

    @Test
    public void shouldThrowExceptionWhenTryingToGetSomethingWithIncorrectId() {
        assertEquals("Java", storageForTest.save(7, "Java"));
        assertEquals(1, storageForTest.getSize());
        assertEquals("Java", storageForTest.getById(7));
        assertEquals("Coffee", storageForTest.update(7, "Coffee"));
        assertThrows(StorageManagementException.class, () -> storageForTest.getById(777));
        assertThrows(StorageManagementException.class, () -> storageForTest.update(777, "Milk"));
        assertThrows(StorageManagementException.class, () -> storageForTest.deleteById(777));
    }

}