package ru.practicum.shareit.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ErrorResponseTest {

    private final ExceptionResponse exceptionResponse = new ExceptionResponse("error", "description");

    @Test
    void getError() {
        assertEquals("error", exceptionResponse.getError());
    }

    @Test
    void getDescription() {
        assertEquals("description", exceptionResponse.getDescription());
    }
}
