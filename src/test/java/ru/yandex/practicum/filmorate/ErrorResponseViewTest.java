package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import ru.yandex.practicum.filmorate.model.presentation.rest_view.ErrorResponseView;

public class ErrorResponseViewTest {
    ErrorResponseView errorResponseView;

    @Test
    public void shouldBeCreatedWithValidFieldsBeEqualToItsCloneAndHaveEqualHashCode() {
        errorResponseView = new ErrorResponseView(1, "", "");
        assertNotNull(errorResponseView);
        ErrorResponseView errorResponseView2 = new ErrorResponseView(1, "", "");
        assertEquals(errorResponseView, errorResponseView2);
        assertEquals(errorResponseView.hashCode(), errorResponseView2.hashCode());
    }

    @Test
    public void shouldBeNotEqualToItsCloneWithSomeDifferencesAndHaveNotSameHashCode() {
        errorResponseView = new ErrorResponseView(1, "", "");
        ErrorResponseView errorResponseView2 = new ErrorResponseView(0, "", "");
        ErrorResponseView errorResponseView3 = new ErrorResponseView(1, "1", "");
        ErrorResponseView errorResponseView4 = new ErrorResponseView(1, "", "1");
        ErrorResponseView errorResponseView5 = new ErrorResponseView(0, " ", "");
        ErrorResponseView errorResponseView6 = new ErrorResponseView(0, "", " ");
        assertNotEquals(errorResponseView, errorResponseView2);
        assertNotEquals(errorResponseView.hashCode(), errorResponseView2.hashCode());
        assertNotEquals(errorResponseView, errorResponseView3);
        assertNotEquals(errorResponseView.hashCode(), errorResponseView3.hashCode());
        assertNotEquals(errorResponseView, errorResponseView4);
        assertNotEquals(errorResponseView.hashCode(), errorResponseView4.hashCode());
        assertNotEquals(errorResponseView, errorResponseView5);
        assertNotEquals(errorResponseView.hashCode(), errorResponseView5.hashCode());
        assertNotEquals(errorResponseView, errorResponseView6);
        assertNotEquals(errorResponseView.hashCode(), errorResponseView6.hashCode());
    }

    @Test
    public void shouldCorrectToStringResult() {
        errorResponseView = new ErrorResponseView(1, "", "");
        System.out.println(errorResponseView);
        assertEquals("ErrorResponseView(statusCode=1, exception=, debugMessage=, errors=null)", errorResponseView.toString());
    }

}