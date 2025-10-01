package ru.practicum.shareit.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    void handleValidationException_ShouldReturnBadRequestWithFirstError() {
        Object target = new Object();
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(target, "target");
        bindingResult.addError(new FieldError("target", "field1", "Field1 is required"));
        bindingResult.addError(new FieldError("target", "field2", "Field2 must be valid"));

        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(null, bindingResult);

        ResponseEntity<ApiError> response = exceptionHandler.handleValidationException(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Field1 is required", response.getBody().getError());
    }

    @Test
    void handleValidationException_WithSingleError_ShouldReturnSingleErrorMessage() {
        Object target = new Object();
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(target, "target");
        bindingResult.addError(new FieldError("target", "name", "Name is required"));

        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(null, bindingResult);

        ResponseEntity<ApiError> response = exceptionHandler.handleValidationException(exception);

        assertEquals("Name is required", response.getBody().getError());
    }

    @Test
    void handleGenericException_ShouldReturnInternalServerErrorWithApiError() {
        String exceptionMessage = "Something went wrong";
        Exception exception = new RuntimeException(exceptionMessage);

        ResponseEntity<ApiError> response = exceptionHandler.handleGenericException(exception);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(exceptionMessage, response.getBody().getError());
    }

    @Test
    void handleGenericException_WithNullMessage_ShouldHandleGracefully() {
        Exception exception = new RuntimeException((String) null);

        ResponseEntity<ApiError> response = exceptionHandler.handleGenericException(exception);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Internal Server Error", response.getBody().getError());
    }

    @Test
    void handleGenericException_WithCustomException_ShouldReturnExceptionMessage() {
        String customMessage = "Custom exception occurred";
        Exception exception = new IllegalArgumentException(customMessage);

        ResponseEntity<ApiError> response = exceptionHandler.handleGenericException(exception);

        assertEquals(customMessage, response.getBody().getError());
    }

    @Test
    void handleValidationException_WithEmptyBindingResult_ShouldReturnDefaultMessage() {
        Object target = new Object();
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(target, "target");

        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(null, bindingResult);

        ResponseEntity<ApiError> response = exceptionHandler.handleValidationException(exception);

        assertEquals("Validation failed", response.getBody().getError());
    }
}