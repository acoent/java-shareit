package ru.practicum.shareit.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.dto.CommentDto;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ItemClientTest {

    private RestTemplate restTemplate;
    private ItemClient itemClient;

    @BeforeEach
    void setUp() {
        restTemplate = mock(RestTemplate.class);
        itemClient = new ItemClient(restTemplate);
    }

    @Test
    void create_update_get_delete_search_comment() {
        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(ResponseEntity.ok().build());

        itemClient.createItem(1L, null);
        verify(restTemplate).exchange(eq("/items"), eq(HttpMethod.POST), any(HttpEntity.class), eq(Object.class));

        itemClient.updateItem(1L, 2L, null);
        verify(restTemplate).exchange(eq("/items/2"), eq(HttpMethod.PATCH), any(HttpEntity.class), eq(Object.class));

        itemClient.getItem(1L, 2L);
        verify(restTemplate).exchange(eq("/items/2"), eq(HttpMethod.GET), any(HttpEntity.class), eq(Object.class));

        itemClient.getUserItems(1L);
        verify(restTemplate).exchange(eq("/items"), eq(HttpMethod.GET), any(HttpEntity.class), eq(Object.class));

        itemClient.searchItems(1L, "t", 0, 10);
        verify(restTemplate).exchange(eq("/items/search?text=t&from=0&size=10"), eq(HttpMethod.GET), any(HttpEntity.class), eq(Object.class));

        itemClient.deleteItem(1L, 2L);
        verify(restTemplate).exchange(eq("/items/2"), eq(HttpMethod.DELETE), any(HttpEntity.class), eq(Object.class));

        CommentDto commentDto = CommentDto.builder().text("c").build();
        itemClient.createComment(1L, 2L, commentDto);
        verify(restTemplate).exchange(eq("/items/2/comment"), eq(HttpMethod.POST), any(HttpEntity.class), eq(Object.class));
    }
}