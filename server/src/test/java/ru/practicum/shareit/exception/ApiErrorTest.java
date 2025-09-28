package ru.practicum.shareit.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ApiErrorTest {

    @Test
    void recordAccessors_Success() {
        String code = "TEST_CODE";
        String message = "Test message";

        ApiError apiError = new ApiError(code, message);

        assertNotNull(apiError);
        assertEquals(code, apiError.code());
        assertEquals(message, apiError.message());
    }
}
