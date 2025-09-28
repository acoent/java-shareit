package ru.practicum.shareit.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.controller.UserController;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@Import(GlobalExceptionHandler.class)
class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void handleNotFoundException() throws Exception {
        when(userService.getById(anyLong())).thenThrow(new NotFoundException("User not found"));

        mockMvc.perform(get("/users/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("User not found"))
                .andExpect(jsonPath("$.code").value("NOT_FOUND"));
    }

    @Test
    void handleBadRequestException() throws Exception {
        UserDto userDto = UserDto.builder().name("John").email("invalid@example.com").build();

        when(userService.create(any(UserDto.class))).thenThrow(new BadRequestException("Invalid data"));

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid data"))
                .andExpect(jsonPath("$.code").value("BAD_REQUEST"));
    }

    @Test
    void handleConflictException() throws Exception {
        when(userService.create(any(UserDto.class))).thenThrow(new ConflictException("Email already exists"));

        UserDto userDto = UserDto.builder().name("John").email("existing@example.com").build();

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Email already exists"))
                .andExpect(jsonPath("$.code").value("CONFLICT"));
    }

    @Test
    void handleForbiddenException() throws Exception {
        when(userService.update(anyLong(), any(UserDto.class))).thenThrow(new ForbiddenException("Access denied"));

        UserDto userDto = UserDto.builder().name("John").build();

        mockMvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("Access denied"))
                .andExpect(jsonPath("$.code").value("FORBIDDEN"));
    }

    @Test
    void handleValidationException() throws Exception {
        UserDto invalidDto = UserDto.builder().build();

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }
}
