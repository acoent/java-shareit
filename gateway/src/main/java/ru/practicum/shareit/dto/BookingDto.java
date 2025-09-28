package ru.practicum.shareit.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingDto {
    private Long id;

    @NotNull(message = "itemId must be provided")
    private Long itemId;

    @NotNull(message = "start must be provided")
    @Future(message = "start must be in the future")
    private LocalDateTime start;

    @NotNull(message = "end must be provided")
    @Future(message = "end must be in the future")
    private LocalDateTime end;
}
