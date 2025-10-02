package ru.practicum.shareit.client;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ru.practicum.shareit.dto.ItemRequestDto;

@Service
public class ItemRequestClient extends BaseClient {
    private static final String API_PREFIX = "/requests";

    public ItemRequestClient(RestTemplate restTemplate) {
        super(restTemplate);
    }

    public ResponseEntity<Object> createRequest(Long userId, ItemRequestDto requestDto) {
        return post(API_PREFIX, requestDto, userId);
    }

    public ResponseEntity<Object> getUserRequests(Long userId) {
        return get(API_PREFIX, null, userId);
    }

    public ResponseEntity<Object> getAllRequests(Long userId, int from, int size) {
        String path = UriComponentsBuilder.fromPath(API_PREFIX)
                .path("/all")
                .queryParam("from", from)
                .queryParam("size", size)
                .toUriString();
        return get(path, null, userId);
    }

    public ResponseEntity<Object> getRequest(Long userId, Long requestId) {
        String path = UriComponentsBuilder.fromPath(API_PREFIX)
                .path("/{requestId}")
                .buildAndExpand(requestId)
                .toUriString();
        return get(path, null, userId);
    }
}