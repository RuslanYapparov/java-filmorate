package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import ru.yandex.practicum.filmorate.model.controllercommandclasses.ErrorResponse;

public class ErrorResponseTest {
    ErrorResponse errorResponse;

    @Test
    public void shouldBeCreatedWithValidFieldsBeEqualToItsCloneAndHaveEqualHashCode() {
        errorResponse = new ErrorResponse(1, "", "");
        assertNotNull(errorResponse);
        ErrorResponse errorResponse2 = new ErrorResponse(1, "", "");
        assertEquals(errorResponse, errorResponse2);
        assertEquals(errorResponse.hashCode(), errorResponse2.hashCode());
    }

    @Test
    public void shouldBeNotEqualToItsCloneWithSomeDifferencesAndHaveNotSameHashCode() {
        errorResponse = new ErrorResponse(1, "", "");
        ErrorResponse errorResponse2 = new ErrorResponse(0, "", "");
        ErrorResponse errorResponse3 = new ErrorResponse(1, "1", "");
        ErrorResponse errorResponse4 = new ErrorResponse(1, "", "1");
        ErrorResponse errorResponse5 = new ErrorResponse(0, " ", "");
        ErrorResponse errorResponse6 = new ErrorResponse(0, "", " ");
        assertNotEquals(errorResponse, errorResponse2);
        assertNotEquals(errorResponse.hashCode(), errorResponse2.hashCode());
        assertNotEquals(errorResponse, errorResponse3);
        assertNotEquals(errorResponse.hashCode(), errorResponse3.hashCode());
        assertNotEquals(errorResponse, errorResponse4);
        assertNotEquals(errorResponse.hashCode(), errorResponse4.hashCode());
        assertNotEquals(errorResponse, errorResponse5);
        assertNotEquals(errorResponse.hashCode(), errorResponse5.hashCode());
        assertNotEquals(errorResponse, errorResponse6);
        assertNotEquals(errorResponse.hashCode(), errorResponse6.hashCode());
    }

    @Test
    public void shouldCorrectToStringResult() {
        errorResponse = new ErrorResponse(1, "", "");
        System.out.println(errorResponse);
        assertEquals("ErrorResponse(statusCode=1, exception=, debugMessage=, errors=null)", errorResponse.toString());
    }

}