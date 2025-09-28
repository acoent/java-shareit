package ru.practicum.shareit.request.mapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ItemRequestMapperTest {

    @Autowired
    private ItemRequestMapper itemRequestMapper;

    @Test
    void toModel_Success() {
        ItemRequestDto dto = ItemRequestDto.builder()
                .id(1L)
                .description("Need a drill")
                .created(LocalDateTime.now())
                .requesterId(5L)
                .build();

        ItemRequest itemRequest = itemRequestMapper.toModel(dto);

        assertNotNull(itemRequest);
        assertNull(itemRequest.getId());
        assertEquals(dto.getDescription(), itemRequest.getDescription());
        assertNull(itemRequest.getCreated());
        assertNull(itemRequest.getRequesterId());
    }

    @Test
    void toDto_Success() {
        ItemRequest itemRequest = ItemRequest.builder()
                .id(1L)
                .description("Need a drill")
                .requesterId(1L)
                .created(LocalDateTime.now())
                .build();

        ItemRequestDto dto = itemRequestMapper.toDto(itemRequest);

        assertNotNull(dto);
        assertEquals(itemRequest.getId(), dto.getId());
        assertEquals(itemRequest.getDescription(), dto.getDescription());
        assertEquals(itemRequest.getCreated(), dto.getCreated());
        assertEquals(itemRequest.getRequesterId(), dto.getRequesterId());
    }
}
