package ru.practicum.shareit.booking.mapper;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

public class BookingMapper {
    public static BookingDto toDto(Booking b) {
        if (b == null) return null;
        BookingDto d = new BookingDto();
        d.setId(b.getId());
        d.setItemId(b.getItemId());
        d.setStart(b.getStart());
        d.setEnd(b.getEnd());
        d.setBookerId(b.getBookerId());
        d.setStatus(b.getStatus().name());
        return d;
    }

    public static Booking toModel(BookingDto d) {
        if (d == null) return null;
        Booking b = new Booking();
        b.setId(d.getId());
        b.setItemId(d.getItemId());
        b.setStart(d.getStart());
        b.setEnd(d.getEnd());
        b.setBookerId(d.getBookerId());
        if (d.getStatus() != null) b.setStatus(BookingStatus.valueOf(d.getStatus()));
        return b;
    }
}
