package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import ru.practicum.shareit.booking.validation.StartBeforeEnd;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@StartBeforeEnd
public class BookingDto {

    @NotNull(message = "itemId must be provided")
    private Long itemId;

    @NotNull(message = "start must be provided")
    @Future(message = "start must be in the future")
    private LocalDateTime start;

    @NotNull(message = "end must be provided")
    @Future(message = "end must be in the future")
    private LocalDateTime end;
}
