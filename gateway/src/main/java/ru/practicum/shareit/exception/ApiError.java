package ru.practicum.shareit.exception;

import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
@Builder
public class ApiError {
    private HttpStatus status;
    private String error;
    private String message;
}
