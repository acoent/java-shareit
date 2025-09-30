package ru.practicum.shareit.client;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.dto.ItemRequestDto;

@Service
public class ItemRequestClient extends BaseClient {

    public ItemRequestClient(RestTemplate restTemplate) {
        super(restTemplate);
    }

    public ResponseEntity<Object> createRequest(Long userId, ItemRequestDto requestDto) {
        return post("/requests", requestDto, userId);
    }

    public ResponseEntity<Object> getUserRequests(Long userId) {
        return get("/requests", null, userId);
    }

    public ResponseEntity<Object> getAllRequests(Long userId, int from, int size) {
        String path = "/requests/all?from=" + from + "&size=" + size;
        return get(path, null, userId);
    }

    public ResponseEntity<Object> getRequest(Long userId, Long requestId) {
        return get("/requests/" + requestId, null, userId);
    }
}