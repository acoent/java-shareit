package ru.practicum.shareit.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.common.HeaderConstants;
import ru.practicum.shareit.dto.CommentDto;
import ru.practicum.shareit.dto.ItemDto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ItemClientTest {

    private RestTemplate restTemplate;
    private ItemClient itemClient;

    @Captor
    private ArgumentCaptor<HttpEntity> httpEntityCaptor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        restTemplate = mock(RestTemplate.class);
        itemClient = new ItemClient(restTemplate);
        httpEntityCaptor = ArgumentCaptor.forClass(HttpEntity.class);
    }

    @Test
    void createItem_shouldCallPostWithCorrectPath() {
        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(ResponseEntity.ok().build());

        itemClient.createItem(1L, null);

        verify(restTemplate).exchange(eq("/items"), eq(HttpMethod.POST), any(HttpEntity.class), eq(Object.class));
    }

    @Test
    void createItem_shouldSetCorrectHeaders() {
        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(ResponseEntity.ok().build());

        ItemDto dto = ItemDto.builder().name("name").description("desc").available(true).build();
        itemClient.createItem(5L, dto);

        verify(restTemplate).exchange(eq("/items"), eq(HttpMethod.POST), httpEntityCaptor.capture(), eq(Object.class));
        assertThat(httpEntityCaptor.getValue().getHeaders().getFirst(HeaderConstants.X_SHARER_USER_ID)).isEqualTo("5");
    }

    @Test
    void updateItem_shouldCallPatchWithCorrectPath() {
        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(ResponseEntity.ok().build());

        itemClient.updateItem(1L, 2L, null);

        verify(restTemplate).exchange(contains("/items/2"), eq(HttpMethod.PATCH), any(HttpEntity.class), eq(Object.class));
    }

    @Test
    void updateItem_shouldBuildCorrectPath() {
        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(ResponseEntity.ok().build());

        ItemDto dto = ItemDto.builder().name("n").description("d").available(true).build();
        itemClient.updateItem(11L, 22L, dto);

        verify(restTemplate).exchange(eq("/items/22"), eq(HttpMethod.PATCH), any(HttpEntity.class), eq(Object.class));
    }

    @Test
    void getItem_shouldCallGetWithCorrectPath() {
        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(ResponseEntity.ok().build());

        itemClient.getItem(1L, 2L);

        verify(restTemplate).exchange(eq("/items/2"), eq(HttpMethod.GET), any(HttpEntity.class), eq(Object.class));
    }

    @Test
    void getItem_shouldBuildCorrectPath() {
        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(ResponseEntity.ok().build());

        itemClient.getItem(11L, 22L);

        verify(restTemplate).exchange(eq("/items/22"), eq(HttpMethod.GET), any(HttpEntity.class), eq(Object.class));
    }

    @Test
    void getUserItems_shouldCallGetWithCorrectPath() {
        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(ResponseEntity.ok().build());

        itemClient.getUserItems(1L);

        verify(restTemplate).exchange(eq("/items"), eq(HttpMethod.GET), any(HttpEntity.class), eq(Object.class));
    }

    @Test
    void searchItems_shouldBuildQueryCorrectly() {
        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(ResponseEntity.ok().build());

        itemClient.searchItems(1L, "t", 0, 10);

        verify(restTemplate).exchange(contains("/items/search"), eq(HttpMethod.GET), any(HttpEntity.class), eq(Object.class));
        verify(restTemplate).exchange(contains("text=t"), eq(HttpMethod.GET), any(HttpEntity.class), eq(Object.class));
        verify(restTemplate).exchange(contains("from=0"), eq(HttpMethod.GET), any(HttpEntity.class), eq(Object.class));
        verify(restTemplate).exchange(contains("size=10"), eq(HttpMethod.GET), any(HttpEntity.class), eq(Object.class));
    }

    @Test
    void searchItems_withNullText_shouldBuildEmptyTextQuery() {
        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(ResponseEntity.ok().build());

        itemClient.searchItems(5L, null, 0, 10);

        verify(restTemplate).exchange(contains("text="), eq(HttpMethod.GET), any(HttpEntity.class), eq(Object.class));
        verify(restTemplate).exchange(contains("from=0"), eq(HttpMethod.GET), any(HttpEntity.class), eq(Object.class));
        verify(restTemplate).exchange(contains("size=10"), eq(HttpMethod.GET), any(HttpEntity.class), eq(Object.class));
    }

    @Test
    void searchItems_shouldSetCorrectHeaders() {
        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(ResponseEntity.ok().build());

        itemClient.searchItems(6L, "text", 0, 10);

        verify(restTemplate).exchange(contains("/items/search"), eq(HttpMethod.GET), httpEntityCaptor.capture(), eq(Object.class));
    }

    @Test
    void deleteItem_shouldCallDeleteWithCorrectPath() {
        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(ResponseEntity.ok().build());

        itemClient.deleteItem(1L, 2L);

        verify(restTemplate).exchange(eq("/items/2"), eq(HttpMethod.DELETE), any(HttpEntity.class), eq(Object.class));
    }

    @Test
    void deleteItem_shouldBuildCorrectPath() {
        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(ResponseEntity.ok().build());

        itemClient.deleteItem(2L, 7L);

        verify(restTemplate).exchange(eq("/items/7"), eq(HttpMethod.DELETE), any(HttpEntity.class), eq(Object.class));
    }

    @Test
    void createComment_shouldCallPostWithCorrectPath() {
        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(ResponseEntity.ok().build());

        CommentDto commentDto = CommentDto.builder().text("c").build();
        itemClient.createComment(1L, 2L, commentDto);

        verify(restTemplate).exchange(contains("/items/2/comment"), eq(HttpMethod.POST), any(HttpEntity.class), eq(Object.class));
    }

    @Test
    void createComment_shouldBuildCorrectPath() {
        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(ResponseEntity.ok().build());

        ItemDto dto = ItemDto.builder().name("a").description("b").available(true).build();
        itemClient.createItem(2L, dto);
        verify(restTemplate).exchange(eq("/items"), eq(HttpMethod.POST), any(HttpEntity.class), eq(Object.class));

        CommentDto commentDto = CommentDto.builder().text("ok").build();
        itemClient.createComment(2L, 7L, commentDto);
        verify(restTemplate).exchange(eq("/items/7/comment"), eq(HttpMethod.POST), any(HttpEntity.class), eq(Object.class));
    }
}