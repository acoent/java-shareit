package ru.practicum.shareit.booking.validation;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class StartBeforeEndValidatorTest {

    private final StartBeforeEndValidator validator = new StartBeforeEndValidator();

    @Test
    void isValid_NullDto_ReturnsTrue() {
        assertTrue(validator.isValid(null, null));
    }

    @Test
    void isValid_NullDates_ReturnsTrue() {
        BookingDto dto = BookingDto.builder().build();
        assertTrue(validator.isValid(dto, null));
    }

    @Test
    void isValid_StartBeforeEnd_ReturnsTrue() {
        BookingDto dto = BookingDto.builder()
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusHours(1))
                .build();
        assertTrue(validator.isValid(dto, null));
    }

    @Test
    void isValid_StartAfterEnd_ReturnsFalse() {
        BookingDto dto = BookingDto.builder()
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now())
                .build();
        assertFalse(validator.isValid(dto, null));
    }
}