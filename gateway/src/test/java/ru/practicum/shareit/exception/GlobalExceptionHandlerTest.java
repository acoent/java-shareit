package ru.practicum.shareit.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    void handleValidationException_ShouldReturnBadRequestWithFieldErrors() {
        Object target = new Object();
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(target, "target");
        bindingResult.addError(new FieldError("target", "field1", "Field1 is required"));
        bindingResult.addError(new FieldError("target", "field2", "Field2 must be valid"));

        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(null, bindingResult);

        ResponseEntity<Object> response = exceptionHandler.handleValidationException(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertInstanceOf(ErrorResponse.class, response.getBody());

        ErrorResponse errorResponse = (ErrorResponse) response.getBody();
        String errorMessage = errorResponse.getError();
        assertTrue(errorMessage.contains("Field1 is required"));
        assertTrue(errorMessage.contains("Field2 must be valid"));
        assertTrue(errorMessage.contains(", "));
    }

    @Test
    void handleValidationException_ShouldJoinMultipleErrorsWithComma() {
        Object target = new Object();
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(target, "target");
        bindingResult.addError(new FieldError("target", "name", "Name is required"));
        bindingResult.addError(new FieldError("target", "email", "Email must be valid"));
        bindingResult.addError(new FieldError("target", "age", "Age must be positive"));

        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(null, bindingResult);

        ResponseEntity<Object> response = exceptionHandler.handleValidationException(exception);

        ErrorResponse errorResponse = (ErrorResponse) response.getBody();
        String errorMessage = errorResponse.getError();

        long commaCount = errorMessage.chars().filter(ch -> ch == ',').count();
        assertEquals(2, commaCount);
    }

    @Test
    void handleValidationException_WithSingleError_ShouldReturnSingleErrorMessage() {
        Object target = new Object();
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(target, "target");
        bindingResult.addError(new FieldError("target", "name", "Name is required"));

        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(null, bindingResult);

        ResponseEntity<Object> response = exceptionHandler.handleValidationException(exception);

        ErrorResponse errorResponse = (ErrorResponse) response.getBody();
        assertEquals("Name is required", errorResponse.getError());
    }

    @Test
    void handleGenericException_ShouldReturnInternalServerErrorWithApiError() {
        String exceptionMessage = "Something went wrong";
        Exception exception = new RuntimeException(exceptionMessage);

        ResponseEntity<ApiError> response = exceptionHandler.handleGenericException(exception);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());

        ApiError apiError = response.getBody();
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, apiError.getStatus());
        assertEquals("Internal Server Error", apiError.getError());
        assertEquals(exceptionMessage, apiError.getMessage());
    }

    @Test
    void handleGenericException_WithNullMessage_ShouldHandleGracefully() {
        Exception exception = new RuntimeException((String) null);

        ResponseEntity<ApiError> response = exceptionHandler.handleGenericException(exception);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());

        ApiError apiError = response.getBody();
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, apiError.getStatus());
        assertEquals("Internal Server Error", apiError.getError());
        assertNull(apiError.getMessage());
    }

    @Test
    void handleGenericException_WithCustomException_ShouldReturnExceptionMessage() {
        String customMessage = "Custom exception occurred";
        Exception exception = new IllegalArgumentException(customMessage);

        ResponseEntity<ApiError> response = exceptionHandler.handleGenericException(exception);

        ApiError apiError = response.getBody();
        assertEquals(customMessage, apiError.getMessage());
    }

    @Test
    void handleValidationException_WithEmptyBindingResult_ShouldReturnEmptyErrorMessage() {
        Object target = new Object();
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(target, "target");

        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(null, bindingResult);

        ResponseEntity<Object> response = exceptionHandler.handleValidationException(exception);

        ErrorResponse errorResponse = (ErrorResponse) response.getBody();
        assertEquals("", errorResponse.getError());
    }

}