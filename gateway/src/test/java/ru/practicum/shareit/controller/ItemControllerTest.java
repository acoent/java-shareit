package ru.practicum.shareit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.client.ItemClient;
import ru.practicum.shareit.dto.ItemDto;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemClient itemClient;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createItem_ShouldReturnCreatedItem() throws Exception {
        Long userId = 1L;
        ItemDto itemDto = ItemDto.builder()
                .name("Drill")
                .description("Powerful drill")
                .available(true)
                .build();

        ItemDto createdItem = ItemDto.builder()
                .id(1L)
                .name("Drill")
                .description("Powerful drill")
                .available(true)
                .build();

        when(itemClient.createItem(eq(userId), any(ItemDto.class)))
                .thenReturn(ResponseEntity.ok(createdItem));

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Drill"))
                .andExpect(jsonPath("$.description").value("Powerful drill"))
                .andExpect(jsonPath("$.available").value(true));
    }

    @Test
    void createItem_WithRequestId_ShouldReturnCreatedItemWithRequestId() throws Exception {
        Long userId = 1L;
        ItemDto itemDto = ItemDto.builder()
                .name("Drill")
                .description("Powerful drill")
                .available(true)
                .requestId(1L)
                .build();

        ItemDto createdItem = ItemDto.builder()
                .id(1L)
                .name("Drill")
                .description("Powerful drill")
                .available(true)
                .requestId(1L)
                .build();

        when(itemClient.createItem(eq(userId), any(ItemDto.class)))
                .thenReturn(ResponseEntity.ok(createdItem));

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.requestId").value(1L));
    }

    @Test
    void createItem_WithInvalidData_ShouldReturnBadRequest() throws Exception {

        Long userId = 1L;
        ItemDto invalidItem = ItemDto.builder()
                .name("")
                .description("")
                .available(null)
                .build();

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidItem)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateItem_ShouldReturnUpdatedItem() throws Exception {
        Long userId = 1L;
        Long itemId = 1L;
        ItemDto itemDto = ItemDto.builder()
                .name("Updated Drill")
                .description("Updated description")
                .available(false)
                .build();

        ItemDto updatedItem = ItemDto.builder()
                .id(itemId)
                .name("Updated Drill")
                .description("Updated description")
                .available(false)
                .build();

        when(itemClient.updateItem(eq(userId), eq(itemId), any(ItemDto.class)))
                .thenReturn(ResponseEntity.ok(updatedItem));

        mockMvc.perform(patch("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemId))
                .andExpect(jsonPath("$.name").value("Updated Drill"))
                .andExpect(jsonPath("$.available").value(false));
    }

    @Test
    void getItem_ShouldReturnItem() throws Exception {
        Long userId = 1L;
        Long itemId = 1L;
        ItemDto item = ItemDto.builder()
                .id(itemId)
                .name("Drill")
                .description("Powerful drill")
                .available(true)
                .build();

        when(itemClient.getItem(userId, itemId))
                .thenReturn(ResponseEntity.ok(item));

        mockMvc.perform(get("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemId))
                .andExpect(jsonPath("$.name").value("Drill"));
    }

    @Test
    void getUserItems_ShouldReturnUserItems() throws Exception {
        Long userId = 1L;
        when(itemClient.getUserItems(userId))
                .thenReturn(ResponseEntity.ok("[]"));

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk());
    }

    @Test
    void searchItems_ShouldReturnSearchResults() throws Exception {
        Long userId = 1L;
        String text = "drill";
        when(itemClient.searchItems(userId, text))
                .thenReturn(ResponseEntity.ok("[]"));

        mockMvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", userId)
                        .param("text", text)
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk());
    }

    @Test
    void deleteItem_ShouldReturnOk() throws Exception {
        Long userId = 1L;
        Long itemId = 1L;
        when(itemClient.deleteItem(userId, itemId))
                .thenReturn(ResponseEntity.ok().build());

        mockMvc.perform(delete("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk());
    }
}
