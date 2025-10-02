package ru.practicum.shareit.booking.mapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class BookingMapperTest {

    @Autowired
    private BookingMapper bookingMapper;

    @Test
    void toModel_Success() {
        BookingDto dto = BookingDto.builder()
                .itemId(1L)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusHours(1))
                .build();

        Booking booking = bookingMapper.toModel(dto);

        assertNotNull(booking);
        assertEquals(dto.getStart(), booking.getStart());
        assertEquals(dto.getEnd(), booking.getEnd());
        assertNull(booking.getItem());
        assertNull(booking.getBooker());
        assertNull(booking.getStatus());
    }

    @Test
    void toResponseDto_Success() {
        User user = User.builder().id(1L).name("John").build();
        Item item = Item.builder().id(1L).name("Item").build();

        Booking booking = Booking.builder()
                .id(1L)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusHours(1))
                .item(item)
                .booker(user)
                .status(BookingStatus.WAITING)
                .build();

        BookingResponseDto dto = bookingMapper.toResponseDto(booking);

        assertNotNull(dto);
        assertEquals(booking.getId(), dto.getId());
        assertEquals(booking.getStart(), dto.getStart());
        assertEquals(booking.getEnd(), dto.getEnd());
        assertEquals(booking.getStatus(), dto.getStatus());
        assertNotNull(dto.getItem());
        assertNotNull(dto.getBooker());
    }
}
