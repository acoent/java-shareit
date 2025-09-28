package ru.practicum.shareit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.client.UserClient;
import ru.practicum.shareit.dto.UserDto;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserClient userClient;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createUser_ShouldReturnCreatedUser() throws Exception {
        // Given
        UserDto userDto = UserDto.builder()
                .name("John Doe")
                .email("john@example.com")
                .build();

        UserDto createdUser = UserDto.builder()
                .id(1L)
                .name("John Doe")
                .email("john@example.com")
                .build();

        when(userClient.createUser(any(UserDto.class)))
                .thenReturn(ResponseEntity.ok(createdUser));

        // When & Then
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.email").value("john@example.com"));
    }

    @Test
    void createUser_WithInvalidData_ShouldReturnBadRequest() throws Exception {
        // Given
        UserDto invalidUser = UserDto.builder()
                .name("") // Invalid: empty name
                .email("invalid-email") // Invalid: malformed email
                .build();

        // When & Then
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidUser)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateUser_ShouldReturnUpdatedUser() throws Exception {
        // Given
        Long userId = 1L;
        UserDto userDto = UserDto.builder()
                .name("Updated Name")
                .email("updated@example.com")
                .build();

        UserDto updatedUser = UserDto.builder()
                .id(userId)
                .name("Updated Name")
                .email("updated@example.com")
                .build();

        when(userClient.updateUser(eq(userId), any(UserDto.class)))
                .thenReturn(ResponseEntity.ok(updatedUser));

        // When & Then
        mockMvc.perform(patch("/users/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.name").value("Updated Name"))
                .andExpect(jsonPath("$.email").value("updated@example.com"));
    }

    @Test
    void getUser_ShouldReturnUser() throws Exception {
        // Given
        Long userId = 1L;
        UserDto user = UserDto.builder()
                .id(userId)
                .name("John Doe")
                .email("john@example.com")
                .build();

        when(userClient.getUser(userId))
                .thenReturn(ResponseEntity.ok(user));

        // When & Then
        mockMvc.perform(get("/users/{userId}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.email").value("john@example.com"));
    }

    @Test
    void getAllUsers_ShouldReturnAllUsers() throws Exception {
        // Given
        when(userClient.getAllUsers())
                .thenReturn(ResponseEntity.ok("[]"));

        // When & Then
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk());
    }

    @Test
    void deleteUser_ShouldReturnOk() throws Exception {
        // Given
        Long userId = 1L;
        when(userClient.deleteUser(userId))
                .thenReturn(ResponseEntity.ok().build());

        // When & Then
        mockMvc.perform(delete("/users/{userId}", userId))
                .andExpect(status().isOk());
    }
}
