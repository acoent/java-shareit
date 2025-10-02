package ru.practicum.shareit.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DtoValidationTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void userDto_WithValidData_ShouldPassValidation() {
        UserDto userDto = UserDto.builder()
                .name("John Doe")
                .email("john@example.com")
                .build();

        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto);
        assertTrue(violations.isEmpty());
    }

    @Test
    void userDto_WithInvalidEmail_ShouldFailValidation() {
        UserDto userDto = UserDto.builder()
                .name("John Doe")
                .email("invalid-email")
                .build();

        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("email must be valid")));
    }

    @Test
    void userDto_WithBlankName_ShouldFailValidation() {
        UserDto userDto = UserDto.builder()
                .name("")
                .email("john@example.com")
                .build();

        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("name must be provided")));
    }

    @Test
    void userDto_WithBlankEmail_ShouldFailValidation() {
        UserDto userDto = UserDto.builder()
                .name("John Doe")
                .email("")
                .build();

        Set<ConstraintViolation<UserDto>> violations = validator.validate(userDto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("email must be provided")));
    }

    @Test
    void itemDto_WithValidData_ShouldPassValidation() {
        ItemDto itemDto = ItemDto.builder()
                .name("Drill")
                .description("Powerful drill")
                .available(true)
                .build();

        Set<ConstraintViolation<ItemDto>> violations = validator.validate(itemDto);
        assertTrue(violations.isEmpty());
    }

    @Test
    void itemDto_WithBlankName_ShouldFailValidation() {
        ItemDto itemDto = ItemDto.builder()
                .name("")
                .description("Powerful drill")
                .available(true)
                .build();

        Set<ConstraintViolation<ItemDto>> violations = validator.validate(itemDto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("name must be provided")));
    }

    @Test
    void itemDto_WithBlankDescription_ShouldFailValidation() {
        ItemDto itemDto = ItemDto.builder()
                .name("Drill")
                .description("")
                .available(true)
                .build();

        Set<ConstraintViolation<ItemDto>> violations = validator.validate(itemDto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("description must be provided")));
    }

    @Test
    void itemDto_WithNullAvailable_ShouldFailValidation() {
        ItemDto itemDto = ItemDto.builder()
                .name("Drill")
                .description("Powerful drill")
                .available(null)
                .build();

        Set<ConstraintViolation<ItemDto>> violations = validator.validate(itemDto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("available must be provided")));
    }

    @Test
    void bookingDto_WithValidData_ShouldPassValidation() {
        BookingDto bookingDto = BookingDto.builder()
                .itemId(1L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();

        Set<ConstraintViolation<BookingDto>> violations = validator.validate(bookingDto);
        assertTrue(violations.isEmpty());
    }

    @Test
    void bookingDto_WithNullItemId_ShouldFailValidation() {
        BookingDto bookingDto = BookingDto.builder()
                .itemId(null)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();

        Set<ConstraintViolation<BookingDto>> violations = validator.validate(bookingDto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("itemId must be provided")));
    }

    @Test
    void bookingDto_WithPastStart_ShouldFailValidation() {
        BookingDto bookingDto = BookingDto.builder()
                .itemId(1L)
                .start(LocalDateTime.now().minusDays(1))
                .end(LocalDateTime.now().plusDays(1))
                .build();

        Set<ConstraintViolation<BookingDto>> violations = validator.validate(bookingDto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("start must be in the future")));
    }

    @Test
    void bookingDto_WithPastEnd_ShouldFailValidation() {
        BookingDto bookingDto = BookingDto.builder()
                .itemId(1L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().minusDays(1))
                .build();

        Set<ConstraintViolation<BookingDto>> violations = validator.validate(bookingDto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("end must be in the future")));
    }

    @Test
    void itemRequestDto_WithValidData_ShouldPassValidation() {
        ItemRequestDto requestDto = ItemRequestDto.builder()
                .description("Need a drill")
                .build();

        Set<ConstraintViolation<ItemRequestDto>> violations = validator.validate(requestDto);
        assertTrue(violations.isEmpty());
    }

    @Test
    void itemRequestDto_WithBlankDescription_ShouldFailValidation() {
        ItemRequestDto requestDto = ItemRequestDto.builder()
                .description("")
                .build();

        Set<ConstraintViolation<ItemRequestDto>> violations = validator.validate(requestDto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("description must be provided")));
    }
}