package ru.practicum.shareit.booking.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.user.dto.UserShortDto;

@Component
public class BookingMapper {

    public BookingResponseDto toResponseDto(Booking b) {
        if (b == null) return null;
        BookingResponseDto r = BookingResponseDto.builder()
                .id(b.getId())
                .start(b.getStart())
                .end(b.getEnd())
                .status(b.getStatus())
                .build();

        if (b.getItem() != null) {
            ItemShortDto itemShort = new ItemShortDto(b.getItem().getId(), b.getItem().getName());
            r.setItem(itemShort);
        }

        if (b.getBooker() != null) {
            UserShortDto userShort = new UserShortDto(b.getBooker().getId(), b.getBooker().getName());
            r.setBooker(userShort);
        }

        return r;
    }

    public Booking toModel(BookingDto d) {
        if (d == null) return null;
        return Booking.builder()
                .id(d.getId())
                .start(d.getStart())
                .end(d.getEnd())
                .status(d.getStatus())
                .build();
    }

    public BookingDto toDto(Booking b) {
        if (b == null) return null;
        BookingDto dto = BookingDto.builder()
                .id(b.getId())
                .itemId(b.getItem() != null ? b.getItem().getId() : null)
                .bookerId(b.getBooker() != null ? b.getBooker().getId() : null)
                .start(b.getStart())
                .end(b.getEnd())
                .status(b.getStatus())
                .build();

        if (b.getItem() != null) dto.setItem(new ItemShortDto(b.getItem().getId(), b.getItem().getName()));
        if (b.getBooker() != null) dto.setBooker(new UserShortDto(b.getBooker().getId(), b.getBooker().getName()));

        return dto;
    }
}
