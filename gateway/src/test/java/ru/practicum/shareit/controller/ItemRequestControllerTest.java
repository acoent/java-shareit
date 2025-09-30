package ru.practicum.shareit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.client.ItemRequestClient;
import ru.practicum.shareit.common.HeaderConstants;
import ru.practicum.shareit.dto.ItemRequestDto;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
class ItemRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemRequestClient itemRequestClient;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createRequest_ShouldReturnCreatedRequest() throws Exception {
        Long userId = 1L;
        ItemRequestDto requestDto = ItemRequestDto.builder()
                .description("Need a drill")
                .build();

        ItemRequestDto createdRequest = ItemRequestDto.builder()
                .id(1L)
                .description("Need a drill")
                .requesterId(userId)
                .build();

        when(itemRequestClient.createRequest(eq(userId), any(ItemRequestDto.class)))
                .thenReturn(ResponseEntity.ok(createdRequest));

        mockMvc.perform(post("/requests")
                        .header(HeaderConstants.X_SHARER_USER_ID, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.description").value("Need a drill"))
                .andExpect(jsonPath("$.requesterId").value(userId));
    }

    @Test
    void createRequest_WithInvalidData_ShouldReturnBadRequest() throws Exception {
        Long userId = 1L;
        ItemRequestDto invalidRequest = ItemRequestDto.builder()
                .description("")
                .build();

        when(itemRequestClient.createRequest(eq(userId), any(ItemRequestDto.class)))
                .thenReturn(ResponseEntity.badRequest().build());

        mockMvc.perform(post("/requests")
                        .header(HeaderConstants.X_SHARER_USER_ID, userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getUserRequests_ShouldReturnUserRequests() throws Exception {
        Long userId = 1L;
        when(itemRequestClient.getUserRequests(userId))
                .thenReturn(ResponseEntity.ok("[]"));

        mockMvc.perform(get("/requests")
                        .header(HeaderConstants.X_SHARER_USER_ID, userId))
                .andExpect(status().isOk());
    }

    @Test
    void getAllRequests_ShouldReturnAllRequests() throws Exception {
        Long userId = 1L;
        when(itemRequestClient.getAllRequests(eq(userId), eq(0), eq(10)))
                .thenReturn(ResponseEntity.ok("[]"));

        mockMvc.perform(get("/requests/all")
                        .header(HeaderConstants.X_SHARER_USER_ID, userId)
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk());
    }

    @Test
    void getRequest_ShouldReturnSpecificRequest() throws Exception {
        Long userId = 1L;
        Long requestId = 1L;
        ItemRequestDto request = ItemRequestDto.builder()
                .id(requestId)
                .description("Need a drill")
                .requesterId(2L)
                .build();

        when(itemRequestClient.getRequest(userId, requestId))
                .thenReturn(ResponseEntity.ok(request));

        mockMvc.perform(get("/requests/{requestId}", requestId)
                        .header(HeaderConstants.X_SHARER_USER_ID, userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(requestId))
                .andExpect(jsonPath("$.description").value("Need a drill"));
    }
}
