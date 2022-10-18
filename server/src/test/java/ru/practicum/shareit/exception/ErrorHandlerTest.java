package ru.practicum.shareit.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ErrorHandlerTest {

    private final ExceptionHandler exceptionHandler = new ExceptionHandler();

    @Test
    void handleConflictException() {
        ConflictException exception = new ConflictException("Внутренний конфликт");
        ExceptionResponse response = exceptionHandler.handleConflictException(exception);
        assertNotNull(response);
        assertEquals(response.getDescription(), exception.getMessage());
    }

    @Test
    void handleWrongParameterException() {
        WrongParameterException exception = new WrongParameterException("Неверное значение");
        ExceptionResponse response = exceptionHandler.handleWrongParameterException(exception);
        assertNotNull(response);
        assertEquals(response.getDescription(), exception.getMessage());
    }

    @Test
    void handleValidationException() {
        ValidationException exception = new ValidationException("Неверный запрос");
        ExceptionResponse response = exceptionHandler.handleValidationException(exception);
        assertNotNull(response);
        assertEquals(response.getDescription(), exception.getMessage());
    }

    @Test
    void handleRunTimeException() {
        RuntimeException exception = new RuntimeException("Ошибка сервера");
        ExceptionResponse response = exceptionHandler.handleRuntimeException(exception);
        assertNotNull(response);
        assertEquals(response.getDescription(), exception.getMessage());
    }
}