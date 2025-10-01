package ru.practicum.shareit.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockitoAnnotations;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.common.HeaderConstants;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class BaseClientTest {

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

    // HTTP Methods Tests
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

    // Error Handling Tests
    @Test
    void httpClientErrorException_withJsonBody_isParsedAndReturned() {
        HttpClientErrorException ex = HttpClientErrorException.create(
                HttpStatus.BAD_REQUEST,
                "Bad Request",
                HttpHeaders.EMPTY,
                "{\"error\":\"boom\"}".getBytes(StandardCharsets.UTF_8),
                StandardCharsets.UTF_8
        );
        when(rest.exchange(anyString(), any(HttpMethod.class), any(), eq(Object.class)))
                .thenThrow(ex);

        ResponseEntity<Object> resp = client.doPost("/p", Map.of("a", 1));

        assertThat(resp.getStatusCodeValue()).isEqualTo(400);
        Object body = resp.getBody();
        assertThat(body).isInstanceOf(Map.class);
        Map<?, ?> map = (Map<?, ?>) body;
        assertThat(map.get("error")).isEqualTo("boom");
    }

    @Test
    void httpClientErrorException_withEmptyBody_returnsEmptyResponseMessage() {
        HttpClientErrorException ex = HttpClientErrorException.create(
                HttpStatus.NOT_FOUND,
                "Not Found",
                HttpHeaders.EMPTY,
                new byte[0],
                StandardCharsets.UTF_8
        );
        when(rest.exchange(anyString(), any(HttpMethod.class), any(), eq(Object.class)))
                .thenThrow(ex);

        ResponseEntity<Object> resp = client.doPost("/p", Map.of("a", 2));

        assertThat(resp.getStatusCodeValue()).isEqualTo(404);
        Object body = resp.getBody();
        assertThat(body).isInstanceOf(Map.class);
        Map<?, ?> map = (Map<?, ?>) body;
        assertThat(map.get("error")).isEqualTo("Empty response from upstream server");
    }

    @Test
    void resourceAccessException_returnsInternalServerErrorWithMessage() {
        when(rest.exchange(anyString(), any(HttpMethod.class), any(), eq(Object.class)))
                .thenThrow(new ResourceAccessException("connection refused"));

        ResponseEntity<Object> resp = client.doGet("/g", null, null);

        assertThat(resp.getStatusCodeValue()).isEqualTo(500);
        Object body = resp.getBody();
        assertThat(body).isInstanceOf(Map.class);
        Map<?, ?> map = (Map<?, ?>) body;
        assertThat(map.get("status")).isEqualTo(500);
        assertThat(map.get("error")).isEqualTo("Internal Server Error");
        assertThat(map.get("message")).asString().contains("connection refused");
    }

    @Test
    void genericException_returnsInternalServerErrorWithMessage() {
        when(rest.exchange(anyString(), any(HttpMethod.class), any(), eq(Object.class)))
                .thenThrow(new RuntimeException("boom"));

        ResponseEntity<Object> resp = client.doDelete("/del/1");

        assertThat(resp.getStatusCodeValue()).isEqualTo(500);
        Object body = resp.getBody();
        assertThat(body).isInstanceOf(Map.class);
        Map<?, ?> map = (Map<?, ?>) body;
        assertThat(map.get("status")).isEqualTo(500);
        assertThat(map.get("error")).isEqualTo("Internal Server Error");
        assertThat(map.get("message")).asString().contains("boom");
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

        public ResponseEntity<Object> doPost(String path, Object body) {
            return post(path, body);
        }
    }
}