package ru.practicum.shareit.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class BaseClientErrorHandlingTest {

    private RestTemplate rest;
    private TestClient client;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        rest = mock(RestTemplate.class);
        client = new TestClient(rest, "http://server");
    }

    @Test
    void httpClientErrorException_withJsonBody_isParsedAndReturned() {
        HttpClientErrorException ex = HttpClientErrorException.create(
                org.springframework.http.HttpStatus.BAD_REQUEST,
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
                org.springframework.http.HttpStatus.NOT_FOUND,
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
        public TestClient(RestTemplate rest, String serverUrl) {
            super(rest);
        }

        public ResponseEntity<Object> doPost(String path, Object body) {
            return post(path, body);
        }

        public ResponseEntity<Object> doGet(String path, java.util.Map<String, Object> parameters, Long userId) {
            return get(path, parameters, userId);
        }

        public ResponseEntity<Object> doDelete(String path) {
            return delete(path);
        }
    }
}
