package ru.practicum.shareit.dto;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DtoBuilderTest {

    @Test
    void userDto_Builder_ShouldCreateCorrectObject() {
        Long id = 1L;
        String name = "John Doe";
        String email = "john@example.com";

        UserDto userDto = UserDto.builder()
                .id(id)
                .name(name)
                .email(email)
                .build();

        assertEquals(id, userDto.getId());
        assertEquals(name, userDto.getName());
        assertEquals(email, userDto.getEmail());
    }

    @Test
    void userDto_NoArgsConstructor_ShouldCreateEmptyObject() {
        UserDto userDto = new UserDto();

        assertNull(userDto.getId());
        assertNull(userDto.getName());
        assertNull(userDto.getEmail());
    }

    @Test
    void userDto_AllArgsConstructor_ShouldCreateCorrectObject() {
        Long id = 1L;
        String name = "John Doe";
        String email = "john@example.com";

        UserDto userDto = new UserDto(id, name, email);

        assertEquals(id, userDto.getId());
        assertEquals(name, userDto.getName());
        assertEquals(email, userDto.getEmail());
    }

    @Test
    void itemDto_Builder_ShouldCreateCorrectObject() {
        Long id = 1L;
        String name = "Drill";
        String description = "Powerful drill";
        Boolean available = true;
        Long requestId = 2L;

        ItemDto itemDto = ItemDto.builder()
                .id(id)
                .name(name)
                .description(description)
                .available(available)
                .requestId(requestId)
                .build();

        assertEquals(id, itemDto.getId());
        assertEquals(name, itemDto.getName());
        assertEquals(description, itemDto.getDescription());
        assertEquals(available, itemDto.getAvailable());
        assertEquals(requestId, itemDto.getRequestId());
    }

    @Test
    void itemDto_NoArgsConstructor_ShouldCreateEmptyObject() {
        ItemDto itemDto = new ItemDto();

        assertNull(itemDto.getId());
        assertNull(itemDto.getName());
        assertNull(itemDto.getDescription());
        assertNull(itemDto.getAvailable());
        assertNull(itemDto.getRequestId());
    }

    @Test
    void itemDto_AllArgsConstructor_ShouldCreateCorrectObject() {
        Long id = 1L;
        String name = "Drill";
        String description = "Powerful drill";
        Boolean available = true;
        Long requestId = 2L;

        ItemDto itemDto = new ItemDto(id, name, description, available, requestId);

        assertEquals(id, itemDto.getId());
        assertEquals(name, itemDto.getName());
        assertEquals(description, itemDto.getDescription());
        assertEquals(available, itemDto.getAvailable());
        assertEquals(requestId, itemDto.getRequestId());
    }

    @Test
    void bookingDto_Builder_ShouldCreateCorrectObject() {
        Long id = 1L;
        Long itemId = 2L;
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);

        BookingDto bookingDto = BookingDto.builder()
                .id(id)
                .itemId(itemId)
                .start(start)
                .end(end)
                .build();

        assertEquals(id, bookingDto.getId());
        assertEquals(itemId, bookingDto.getItemId());
        assertEquals(start, bookingDto.getStart());
        assertEquals(end, bookingDto.getEnd());
    }

    @Test
    void bookingDto_NoArgsConstructor_ShouldCreateEmptyObject() {
        BookingDto bookingDto = new BookingDto();

        assertNull(bookingDto.getId());
        assertNull(bookingDto.getItemId());
        assertNull(bookingDto.getStart());
        assertNull(bookingDto.getEnd());
    }

    @Test
    void bookingDto_AllArgsConstructor_ShouldCreateCorrectObject() {
        Long id = 1L;
        Long itemId = 2L;
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);

        BookingDto bookingDto = new BookingDto(id, itemId, start, end);

        assertEquals(id, bookingDto.getId());
        assertEquals(itemId, bookingDto.getItemId());
        assertEquals(start, bookingDto.getStart());
        assertEquals(end, bookingDto.getEnd());
    }

    @Test
    void itemRequestDto_Builder_ShouldCreateCorrectObject() {
        Long id = 1L;
        String description = "Need a drill";
        Long requesterId = 2L;
        LocalDateTime created = LocalDateTime.now();
        List<ItemDto> items = List.of(new ItemDto());

        ItemRequestDto requestDto = ItemRequestDto.builder()
                .id(id)
                .description(description)
                .requesterId(requesterId)
                .created(created)
                .items(items)
                .build();

        assertEquals(id, requestDto.getId());
        assertEquals(description, requestDto.getDescription());
        assertEquals(requesterId, requestDto.getRequesterId());
        assertEquals(created, requestDto.getCreated());
        assertEquals(items, requestDto.getItems());
    }

    @Test
    void itemRequestDto_NoArgsConstructor_ShouldCreateEmptyObject() {
        ItemRequestDto requestDto = new ItemRequestDto();

        assertNull(requestDto.getId());
        assertNull(requestDto.getDescription());
        assertNull(requestDto.getRequesterId());
        assertNull(requestDto.getCreated());
        assertNull(requestDto.getItems());
    }

    @Test
    void itemRequestDto_AllArgsConstructor_ShouldCreateCorrectObject() {
        Long id = 1L;
        String description = "Need a drill";
        Long requesterId = 2L;
        LocalDateTime created = LocalDateTime.now();
        List<ItemDto> items = List.of(new ItemDto());

        ItemRequestDto requestDto = new ItemRequestDto(id, description, requesterId, created, items);

        assertEquals(id, requestDto.getId());
        assertEquals(description, requestDto.getDescription());
        assertEquals(requesterId, requestDto.getRequesterId());
        assertEquals(created, requestDto.getCreated());
        assertEquals(items, requestDto.getItems());
    }
}