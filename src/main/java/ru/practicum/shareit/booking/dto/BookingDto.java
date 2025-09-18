package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.validation.StartBeforeEnd;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@StartBeforeEnd
public class BookingDto {

    private Long id;

    @NotNull(message = "itemId must be provided")
    private Long itemId;

    private ru.practicum.shareit.item.dto.ItemShortDto item;
    private Long bookerId;
    private ru.practicum.shareit.user.dto.UserShortDto booker;

    @NotNull(message = "start must be provided")
    private LocalDateTime start;

    @NotNull(message = "end must be provided")
    private LocalDateTime end;

    private BookingStatus status;
}
