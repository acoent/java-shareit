package ru.practicum.shareit.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.dto.ItemDto;
import ru.practicum.shareit.dto.ItemRequestDto;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ClientsExtraTest {

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void itemClient_updateAndGetAndDelete() {
        RestTemplate rest = mock(RestTemplate.class);
        when(rest.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(ResponseEntity.ok().build());
        ItemClient client = new ItemClient(rest);
        ItemDto dto = ItemDto.builder().name("n").description("d").available(true).build();
        client.updateItem(11L, 22L, dto);
        verify(rest).exchange(eq("/items/22"), eq(HttpMethod.PATCH), any(HttpEntity.class), eq(Object.class));
        client.getItem(11L, 22L);
        verify(rest).exchange(eq("/items/22"), eq(HttpMethod.GET), any(HttpEntity.class), eq(Object.class));
        client.deleteItem(11L, 22L);
        verify(rest).exchange(eq("/items/22"), eq(HttpMethod.DELETE), any(HttpEntity.class), eq(Object.class));
    }

    @Test
    void itemRequestClient_getAndCreate() {
        RestTemplate rest = mock(RestTemplate.class);
        when(rest.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(ResponseEntity.ok().build());
        ItemRequestClient client = new ItemRequestClient(rest);
        client.getAllRequests(5L, 1, 20);
        verify(rest).exchange(eq("/requests/all?from=1&size=20"), eq(HttpMethod.GET), any(HttpEntity.class), eq(Object.class));

        ItemRequestDto dto = ItemRequestDto.builder().description("desc").created(LocalDateTime.now()).build();
        client.createRequest(5L, dto);
        verify(rest).exchange(eq("/requests"), eq(HttpMethod.POST), any(HttpEntity.class), eq(Object.class));
    }
}