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
class UserDtoJsonTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void serialize_ShouldSerializeCorrectly() throws IOException {
        UserDto userDto = UserDto.builder()
                .id(1L)
                .name("John Doe")
                .email("john@example.com")
                .build();

        String json = objectMapper.writeValueAsString(userDto);

        assertNotNull(json);
        assertTrue(json.contains("\"id\":1"));
        assertTrue(json.contains("\"name\":\"John Doe\""));
        assertTrue(json.contains("\"email\":\"john@example.com\""));
    }

    @Test
    void deserialize_ShouldDeserializeCorrectly() throws IOException {
        String json = "{\"id\":1,\"name\":\"John Doe\",\"email\":\"john@example.com\"}";

        UserDto userDto = objectMapper.readValue(json, UserDto.class);

        assertNotNull(userDto);
        assertEquals(1L, userDto.getId());
        assertEquals("John Doe", userDto.getName());
        assertEquals("john@example.com", userDto.getEmail());
    }

    @Test
    void deserialize_WithNullId_ShouldDeserializeCorrectly() throws IOException {
        String json = "{\"id\":null,\"name\":\"John Doe\",\"email\":\"john@example.com\"}";

        UserDto userDto = objectMapper.readValue(json, UserDto.class);

        assertNotNull(userDto);
        assertNull(userDto.getId());
        assertEquals("John Doe", userDto.getName());
        assertEquals("john@example.com", userDto.getEmail());
    }

    @Test
    void roundTrip_ShouldMaintainDataIntegrity() throws IOException {
        UserDto originalUser = UserDto.builder()
                .id(1L)
                .name("Jane Smith")
                .email("jane@example.com")
                .build();

        String json = objectMapper.writeValueAsString(originalUser);
        UserDto deserializedUser = objectMapper.readValue(json, UserDto.class);

        assertEquals(originalUser.getId(), deserializedUser.getId());
        assertEquals(originalUser.getName(), deserializedUser.getName());
        assertEquals(originalUser.getEmail(), deserializedUser.getEmail());
    }

    @Test
    void serialize_WithNullFields_ShouldIncludeNullValues() throws IOException {
        UserDto userDto = UserDto.builder()
                .id(null)
                .name(null)
                .email(null)
                .build();

        String json = objectMapper.writeValueAsString(userDto);

        assertTrue(json.contains("\"id\":null"));
        assertTrue(json.contains("\"name\":null"));
        assertTrue(json.contains("\"email\":null"));
    }
}