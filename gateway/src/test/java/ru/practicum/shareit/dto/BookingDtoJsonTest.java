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
class BookingDtoJsonTest {

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    void serialize_ShouldSerializeCorrectly() throws IOException {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        BookingDto bookingDto = BookingDto.builder()
                .id(1L)
                .itemId(1L)
                .start(start)
                .end(end)
                .build();

        String json = objectMapper.writeValueAsString(bookingDto);

        assertNotNull(json);
        assertTrue(json.contains("\"id\":1"));
        assertTrue(json.contains("\"itemId\":1"));
        assertTrue(json.contains("\"start\""));
        assertTrue(json.contains("\"end\""));
    }

    @Test
    void deserialize_ShouldDeserializeCorrectly() throws IOException {
        String json = "{\"id\":1,\"itemId\":1,\"start\":\"2023-01-01T12:00:00\",\"end\":\"2023-01-02T12:00:00\"}";
        BookingDto bookingDto = objectMapper.readValue(json, BookingDto.class);
        assertNotNull(bookingDto);
        assertEquals(1L, bookingDto.getId());
        assertEquals(1L, bookingDto.getItemId());
        assertNotNull(bookingDto.getStart());
        assertNotNull(bookingDto.getEnd());
    }

    @Test
    void deserialize_WithNullFields_ShouldDeserializeCorrectly() throws IOException {
        String json = "{\"id\":null,\"itemId\":1,\"start\":\"2023-01-01T12:00:00\",\"end\":\"2023-01-02T12:00:00\"}";
        BookingDto bookingDto = objectMapper.readValue(json, BookingDto.class);
        assertNotNull(bookingDto);
        assertNull(bookingDto.getId());
        assertEquals(1L, bookingDto.getItemId());
        assertNotNull(bookingDto.getStart());
        assertNotNull(bookingDto.getEnd());
    }

    @Test
    void roundTrip_ShouldMaintainDataIntegrity() throws IOException {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        BookingDto originalBooking = BookingDto.builder()
                .id(1L)
                .itemId(1L)
                .start(start)
                .end(end)
                .build();
        String json = objectMapper.writeValueAsString(originalBooking);
        BookingDto deserializedBooking = objectMapper.readValue(json, BookingDto.class);
        assertEquals(originalBooking.getId(), deserializedBooking.getId());
        assertEquals(originalBooking.getItemId(), deserializedBooking.getItemId());
        assertEquals(originalBooking.getStart(), deserializedBooking.getStart());
        assertEquals(originalBooking.getEnd(), deserializedBooking.getEnd());
    }
}
