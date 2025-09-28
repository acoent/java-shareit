package ru.practicum.shareit.client;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

import static org.mockito.Mockito.mock;

class ItemClientTest {

    private RestTemplate restTemplate;
    private ItemClient itemClient;

    @BeforeEach
    void setUp() {
        restTemplate = mock(RestTemplate.class);
        itemClient = new ItemClient("http://localhost:8080") {
            private ResponseEntity<Object> makeAndSendRequest(HttpMethod method, String path, Map<String, Object> parameters, Object body, Long userId) {
                return ResponseEntity.ok(body);
            }
        };
    }
}
