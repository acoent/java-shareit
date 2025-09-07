package ru.practicum.shareit.booking.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.practicum.shareit.booking.dto.BookingDto;

public class StartBeforeEndValidator implements ConstraintValidator<StartBeforeEnd, BookingDto> {

    @Override
    public boolean isValid(BookingDto dto, ConstraintValidatorContext context) {
        if (dto == null) return true;
        if (dto.getStart() == null || dto.getEnd() == null) return true;
        return dto.getStart().isBefore(dto.getEnd());
    }
}
