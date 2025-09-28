package ru.practicum.shareit.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@JsonTest
@SpringJUnitConfig
class ItemRequestDtoJsonTest {

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    void serialize_ShouldSerializeCorrectly() throws IOException {
        LocalDateTime now = LocalDateTime.now();
        ItemRequestDto requestDto = ItemRequestDto.builder()
                .id(1L)
                .description("Need a drill")
                .requesterId(1L)
                .created(now)
                .build();
        String json = objectMapper.writeValueAsString(requestDto);

        assertNotNull(json);
        assertTrue(json.contains("\"id\":1"));
        assertTrue(json.contains("\"description\":\"Need a drill\""));
        assertTrue(json.contains("\"requesterId\":1"));
        assertTrue(json.contains("\"created\""));
    }

    @Test
    void deserialize_ShouldDeserializeCorrectly() throws IOException {
        String json = "{\"id\":1,\"description\":\"Need a drill\",\"requesterId\":1,\"created\":\"2023-01-01T12:00:00\"}";
        ItemRequestDto requestDto = objectMapper.readValue(json, ItemRequestDto.class);

        assertNotNull(requestDto);
        assertEquals(1L, requestDto.getId());
        assertEquals("Need a drill", requestDto.getDescription());
        assertEquals(1L, requestDto.getRequesterId());
        assertNotNull(requestDto.getCreated());
    }

    @Test
    void deserialize_WithNullFields_ShouldDeserializeCorrectly() throws IOException {
        String json = "{\"id\":null,\"description\":\"Need a drill\",\"requesterId\":null,\"created\":null}";
        ItemRequestDto requestDto = objectMapper.readValue(json, ItemRequestDto.class);

        assertNotNull(requestDto);
        assertNull(requestDto.getId());
        assertEquals("Need a drill", requestDto.getDescription());
        assertNull(requestDto.getRequesterId());
        assertNull(requestDto.getCreated());
    }

    @Test
    void roundTrip_ShouldMaintainDataIntegrity() throws IOException {
        LocalDateTime now = LocalDateTime.now();
        ItemRequestDto originalRequest = ItemRequestDto.builder()
                .id(1L)
                .description("Need a drill")
                .requesterId(1L)
                .created(now)
                .build();

        String json = objectMapper.writeValueAsString(originalRequest);
        ItemRequestDto deserializedRequest = objectMapper.readValue(json, ItemRequestDto.class);

        assertEquals(originalRequest.getId(), deserializedRequest.getId());
        assertEquals(originalRequest.getDescription(), deserializedRequest.getDescription());
        assertEquals(originalRequest.getRequesterId(), deserializedRequest.getRequesterId());
        assertEquals(originalRequest.getCreated(), deserializedRequest.getCreated());
    }
}
