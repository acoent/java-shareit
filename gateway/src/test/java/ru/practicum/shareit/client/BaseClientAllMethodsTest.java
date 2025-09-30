package ru.practicum.shareit.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockitoAnnotations;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.common.HeaderConstants;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class BaseClientAllMethodsTest {

    private RestTemplate rest;
    private TestClient client;
    private ArgumentCaptor<HttpEntity> httpEntityCaptor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        rest = mock(RestTemplate.class);
        client = new TestClient(rest);
        httpEntityCaptor = ArgumentCaptor.forClass(HttpEntity.class);
    }

    @Test
    void put_withoutUserId_shouldCallExchangeWithoutUserHeader() {
        when(rest.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(ResponseEntity.ok().build());
        client.doPut("/p", Map.of("a", 1));
        verify(rest).exchange(eq("/p"), eq(HttpMethod.PUT), httpEntityCaptor.capture(), eq(Object.class));
        HttpHeaders h = httpEntityCaptor.getValue().getHeaders();
        assertThat(h.getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
        assertThat(h.containsKey(HeaderConstants.X_SHARER_USER_ID)).isFalse();
    }

    @Test
    void put_withUserId_shouldIncludeUserHeader() {
        when(rest.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(ResponseEntity.ok().build());
        client.doPut("/p/1", Map.of("a", 2), 99L);
        verify(rest).exchange(eq("/p/1"), eq(HttpMethod.PUT), httpEntityCaptor.capture(), eq(Object.class));
        assertThat(httpEntityCaptor.getValue().getHeaders().getFirst(HeaderConstants.X_SHARER_USER_ID)).isEqualTo("99");
    }

    @Test
    void patch_withoutUserId_shouldNotIncludeUserHeader() {
        when(rest.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(ResponseEntity.ok().build());
        client.doPatch("/p/2", Map.of("k", "v"));
        verify(rest).exchange(eq("/p/2"), eq(HttpMethod.PATCH), httpEntityCaptor.capture(), eq(Object.class));
        assertThat(httpEntityCaptor.getValue().getHeaders().containsKey(HeaderConstants.X_SHARER_USER_ID)).isFalse();
    }

    @Test
    void delete_withoutUserId_shouldCallDeleteExchange() {
        when(rest.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(ResponseEntity.noContent().build());
        client.doDelete("/d/1");
        verify(rest).exchange(eq("/d/1"), eq(HttpMethod.DELETE), httpEntityCaptor.capture(), eq(Object.class));
        assertThat(httpEntityCaptor.getValue().getHeaders().getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
    }

    @Test
    void get_withParameters_andUserId_shouldCallExchangeWithUriVariables() {
        when(rest.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), eq(Object.class), anyMap()))
                .thenReturn(ResponseEntity.ok().build());
        client.doGet("/g/{x}", Map.of("x", 5), 123L);
        verify(rest).exchange(eq("/g/{x}"), eq(HttpMethod.GET), httpEntityCaptor.capture(), eq(Object.class), eq(Map.of("x", 5)));
        HttpHeaders h = httpEntityCaptor.getValue().getHeaders();
        assertThat(h.getFirst(HeaderConstants.X_SHARER_USER_ID)).isEqualTo("123");
        assertThat(h.getContentType()).isEqualTo(MediaType.APPLICATION_JSON);
    }

    static class TestClient extends BaseClient {
        public TestClient(RestTemplate rest) {
            super(rest);
        }

        public ResponseEntity<Object> doPut(String path, Object body) {
            return put(path, body);
        }

        public ResponseEntity<Object> doPut(String path, Object body, Long userId) {
            return put(path, body, userId);
        }

        public ResponseEntity<Object> doPatch(String path, Object body) {
            return patch(path, body);
        }

        public ResponseEntity<Object> doDelete(String path) {
            return delete(path);
        }

        public ResponseEntity<Object> doGet(String path, Map<String, Object> parameters, Long userId) {
            return get(path, parameters, userId);
        }
    }
}