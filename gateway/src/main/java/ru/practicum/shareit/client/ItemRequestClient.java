package ru.practicum.shareit.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.dto.ItemRequestDto;

import java.util.Map;

@Service
public class ItemRequestClient extends BaseClient {

    public ItemRequestClient(@Value("${shareit.server.url}") String serverUrl) {
        super(new org.springframework.web.client.RestTemplate(), serverUrl);
    }

    public ResponseEntity<Object> createRequest(Long userId, ItemRequestDto requestDto) {
        return post("/requests", requestDto, userId);
    }

    public ResponseEntity<Object> getUserRequests(Long userId) {
        return get("/requests", null, userId);
    }

    public ResponseEntity<Object> getAllRequests(Long userId, int from, int size) {
        Map<String, Object> parameters = Map.of("from", from, "size", size);
        return get("/requests/all", parameters, userId);
    }

    public ResponseEntity<Object> getRequest(Long userId, Long requestId) {
        return get("/requests/" + requestId, null, userId);
    }
}
