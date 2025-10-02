package ru.practicum.shareit.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@JsonTest
@SpringJUnitConfig
class ItemDtoJsonTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void serialize_ShouldSerializeCorrectly() throws IOException {
        ItemDto itemDto = ItemDto.builder()
                .id(1L)
                .name("Drill")
                .description("Powerful drill for construction")
                .available(true)
                .requestId(1L)
                .build();

        String json = objectMapper.writeValueAsString(itemDto);

        assertNotNull(json);
        assertTrue(json.contains("\"id\":1"));
        assertTrue(json.contains("\"name\":\"Drill\""));
        assertTrue(json.contains("\"description\":\"Powerful drill for construction\""));
        assertTrue(json.contains("\"available\":true"));
        assertTrue(json.contains("\"requestId\":1"));
    }

    @Test
    void deserialize_ShouldDeserializeCorrectly() throws IOException {
        String json = "{\"id\":1,\"name\":\"Drill\",\"description\":\"Powerful drill for construction\",\"available\":true,\"requestId\":1}";

        ItemDto itemDto = objectMapper.readValue(json, ItemDto.class);

        assertNotNull(itemDto);
        assertEquals(1L, itemDto.getId());
        assertEquals("Drill", itemDto.getName());
        assertEquals("Powerful drill for construction", itemDto.getDescription());
        assertTrue(itemDto.getAvailable());
        assertEquals(1L, itemDto.getRequestId());
    }

    @Test
    void deserialize_WithNullRequestId_ShouldDeserializeCorrectly() throws IOException {
        String json = "{\"id\":1,\"name\":\"Drill\",\"description\":\"Powerful drill for construction\",\"available\":true,\"requestId\":null}";

        ItemDto itemDto = objectMapper.readValue(json, ItemDto.class);

        assertNotNull(itemDto);
        assertEquals(1L, itemDto.getId());
        assertEquals("Drill", itemDto.getName());
        assertEquals("Powerful drill for construction", itemDto.getDescription());
        assertTrue(itemDto.getAvailable());
        assertNull(itemDto.getRequestId());
    }

    @Test
    void roundTrip_ShouldMaintainDataIntegrity() throws IOException {
        ItemDto originalItem = ItemDto.builder()
                .id(1L)
                .name("Drill")
                .description("Powerful drill for construction")
                .available(true)
                .requestId(1L)
                .build();

        String json = objectMapper.writeValueAsString(originalItem);
        ItemDto deserializedItem = objectMapper.readValue(json, ItemDto.class);

        assertEquals(originalItem.getId(), deserializedItem.getId());
        assertEquals(originalItem.getName(), deserializedItem.getName());
        assertEquals(originalItem.getDescription(), deserializedItem.getDescription());
        assertEquals(originalItem.getAvailable(), deserializedItem.getAvailable());
        assertEquals(originalItem.getRequestId(), deserializedItem.getRequestId());
    }
}
