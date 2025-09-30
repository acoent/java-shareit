package ru.practicum.shareit.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.dto.CommentDto;
import ru.practicum.shareit.dto.ItemDto;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ItemClientSearchNullTextTest {

    private RestTemplate rest;
    private ItemClient itemClient;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        rest = mock(RestTemplate.class);
        itemClient = new ItemClient(rest);
    }

    @Test
    void searchItems_withNullText_buildsEmptyTextQuery() {
        when(rest.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(ResponseEntity.ok().build());

        itemClient.searchItems(5L, null, 0, 10);

        verify(rest).exchange(eq("/items/search?text=&from=0&size=10"), eq(HttpMethod.GET), any(HttpEntity.class), eq(Object.class));
    }

    @Test
    void createAndDeleteComment_andCreateItem_paths_areCorrect() {
        when(rest.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(ResponseEntity.ok().build());

        ItemDto dto = ItemDto.builder().name("a").description("b").available(true).build();
        itemClient.createItem(2L, dto);
        verify(rest).exchange(eq("/items"), eq(HttpMethod.POST), any(HttpEntity.class), eq(Object.class));

        CommentDto commentDto = CommentDto.builder().text("ok").build();
        itemClient.createComment(2L, 7L, commentDto);
        verify(rest).exchange(eq("/items/7/comment"), eq(HttpMethod.POST), any(HttpEntity.class), eq(Object.class));

        itemClient.deleteItem(2L, 7L);
        verify(rest).exchange(eq("/items/7"), eq(HttpMethod.DELETE), any(HttpEntity.class), eq(Object.class));
    }
}