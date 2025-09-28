package ru.practicum.shareit.request.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.common.HeaderConstants;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemRequestController.class)
class ItemRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemRequestService itemRequestService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void create_Success() throws Exception {
        ItemRequestDto requestDto = ItemRequestDto.builder()
                .description("Need a drill")
                .build();

        ItemRequestDto responseDto = ItemRequestDto.builder()
                .id(1L)
                .description("Need a drill")
                .requesterId(1L)
                .created(LocalDateTime.now())
                .items(Collections.emptyList())
                .build();

        when(itemRequestService.create(eq(1L), any(ItemRequestDto.class))).thenReturn(responseDto);

        mockMvc.perform(post("/requests")
                        .header(HeaderConstants.X_SHARER_USER_ID, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.description").value("Need a drill"));
    }

    @Test
    void getByRequester_Success() throws Exception {
        ItemRequestDto responseDto = ItemRequestDto.builder()
                .id(1L)
                .description("Need a drill")
                .created(LocalDateTime.now())
                .items(Collections.emptyList())
                .build();

        when(itemRequestService.getByRequester(1L)).thenReturn(List.of(responseDto));

        mockMvc.perform(get("/requests")
                        .header(HeaderConstants.X_SHARER_USER_ID, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    void getAll_Success() throws Exception {
        ItemRequestDto responseDto = ItemRequestDto.builder()
                .id(1L)
                .description("Need a drill")
                .build();

        when(itemRequestService.getAll(eq(1L), anyInt(), anyInt())).thenReturn(List.of(responseDto));

        mockMvc.perform(get("/requests/all")
                        .header(HeaderConstants.X_SHARER_USER_ID, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void getById_Success() throws Exception {
        ItemRequestDto responseDto = ItemRequestDto.builder()
                .id(1L)
                .description("Need a drill")
                .build();

        when(itemRequestService.getById(1L, 1L)).thenReturn(responseDto);

        mockMvc.perform(get("/requests/1")
                        .header(HeaderConstants.X_SHARER_USER_ID, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }
}
