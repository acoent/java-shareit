package ru.practicum.shareit.item.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.common.HeaderConstants;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.service.ItemService;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemService itemService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void create_Success() throws Exception {
        ItemDto itemDto = ItemDto.builder()
                .name("Test Item")
                .description("Test Description")
                .available(true)
                .build();

        ItemDto responseDto = ItemDto.builder()
                .id(1L)
                .name("Test Item")
                .description("Test Description")
                .available(true)
                .build();

        when(itemService.create(eq(1L), any(ItemDto.class))).thenReturn(responseDto);

        mockMvc.perform(post("/items")
                        .header(HeaderConstants.X_SHARER_USER_ID, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test Item"));
    }

    @Test
    void update_Success() throws Exception {
        ItemDto updateDto = ItemDto.builder()
                .name("Updated Item")
                .build();

        ItemDto responseDto = ItemDto.builder()
                .id(1L)
                .name("Updated Item")
                .description("Test Description")
                .available(true)
                .build();

        when(itemService.update(eq(1L), eq(1L), any(ItemDto.class))).thenReturn(responseDto);

        mockMvc.perform(patch("/items/1")
                        .header(HeaderConstants.X_SHARER_USER_ID, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Item"));
    }

    @Test
    void getById_Success() throws Exception {
        ItemResponseDto responseDto = ItemResponseDto.builder()
                .id(1L)
                .name("Test Item")
                .description("Test Description")
                .available(true)
                .build();

        when(itemService.getById(1L, 1L)).thenReturn(responseDto);

        mockMvc.perform(get("/items/1")
                        .header(HeaderConstants.X_SHARER_USER_ID, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test Item"));
    }

    @Test
    void getByOwner_Success() throws Exception {
        ItemResponseDto responseDto = ItemResponseDto.builder()
                .id(1L)
                .name("Test Item")
                .build();

        when(itemService.getByOwner(eq(1L), anyInt(), anyInt())).thenReturn(List.of(responseDto));

        mockMvc.perform(get("/items")
                        .header(HeaderConstants.X_SHARER_USER_ID, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    void search_Success() throws Exception {
        ItemDto itemDto = ItemDto.builder()
                .id(1L)
                .name("Test Item")
                .build();

        when(itemService.search(eq("test"), anyInt(), anyInt())).thenReturn(List.of(itemDto));

        mockMvc.perform(get("/items/search")
                        .param("text", "test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    void addComment_Success() throws Exception {
        CommentDto commentDto = CommentDto.builder()
                .text("Great item!")
                .build();

        CommentDto responseDto = CommentDto.builder()
                .id(1L)
                .text("Great item!")
                .authorName("John Doe")
                .created(LocalDateTime.now())
                .build();

        when(itemService.addComment(eq(1L), eq(1L), any(CommentDto.class))).thenReturn(responseDto);

        mockMvc.perform(post("/items/1/comment")
                        .header(HeaderConstants.X_SHARER_USER_ID, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.text").value("Great item!"));
    }
}