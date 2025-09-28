package ru.practicum.shareit.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ExceptionClassesTest {

    @Test
    void badRequestException() {
        String message = "Bad request";
        BadRequestException exception = new BadRequestException(message);

        assertEquals(message, exception.getMessage());
        assertInstanceOf(RuntimeException.class, exception);
    }

    @Test
    void conflictException() {
        String message = "Conflict occurred";
        ConflictException exception = new ConflictException(message);

        assertEquals(message, exception.getMessage());
        assertInstanceOf(RuntimeException.class, exception);
    }

    @Test
    void forbiddenException() {
        String message = "Access forbidden";
        ForbiddenException exception = new ForbiddenException(message);

        assertEquals(message, exception.getMessage());
        assertInstanceOf(RuntimeException.class, exception);
    }

    @Test
    void notFoundException() {
        String message = "Not found";
        NotFoundException exception = new NotFoundException(message);

        assertEquals(message, exception.getMessage());
        assertInstanceOf(RuntimeException.class, exception);
    }
}