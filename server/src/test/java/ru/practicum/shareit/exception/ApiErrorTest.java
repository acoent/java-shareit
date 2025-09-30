package ru.practicum.shareit.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ApiErrorTest {

    @Test
    void builderAndAccessors_Success() {
        String error = "Test error message";

        ApiError apiError = ApiError.builder()
                .error(error)
                .build();

        assertNotNull(apiError);
        assertEquals(error, apiError.getError());
    }

    @Test
    void allArgsConstructor_Success() {
        String error = "Another error";

        ApiError apiError = new ApiError(error);

        assertNotNull(apiError);
        assertEquals(error, apiError.getError());
    }

    @Test
    void noArgsConstructor_Success() {
        ApiError apiError = new ApiError();

        assertNotNull(apiError);
    }

    @Test
    void setterAndGetter_Success() {
        ApiError apiError = new ApiError();
        String error = "Error via setter";

        apiError.setError(error);

        assertEquals(error, apiError.getError());
    }
}