package ru.practicum.shareit.client;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ru.practicum.shareit.dto.CommentDto;
import ru.practicum.shareit.dto.ItemDto;

@Service
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    public ItemClient(RestTemplate restTemplate) {
        super(restTemplate);
    }

    public ResponseEntity<Object> createItem(Long userId, ItemDto itemDto) {
        return post(API_PREFIX, itemDto, userId);
    }

    public ResponseEntity<Object> updateItem(Long userId, Long itemId, ItemDto itemDto) {
        String path = UriComponentsBuilder.fromPath(API_PREFIX)
                .path("/{itemId}")
                .buildAndExpand(itemId)
                .toUriString();
        return patch(path, itemDto, userId);
    }

    public ResponseEntity<Object> getItem(Long userId, Long itemId) {
        String path = UriComponentsBuilder.fromPath(API_PREFIX)
                .path("/{itemId}")
                .buildAndExpand(itemId)
                .toUriString();
        return get(path, null, userId);
    }

    public ResponseEntity<Object> getUserItems(Long userId) {
        return get(API_PREFIX, null, userId);
    }

    public ResponseEntity<Object> searchItems(Long userId, String text, int from, int size) {
        String path = UriComponentsBuilder.fromPath(API_PREFIX)
                .path("/search")
                .queryParam("text", text == null ? "" : text)
                .queryParam("from", from)
                .queryParam("size", size)
                .toUriString();
        return get(path, null, userId);
    }

    public ResponseEntity<Object> deleteItem(Long userId, Long itemId) {
        String path = UriComponentsBuilder.fromPath(API_PREFIX)
                .path("/{itemId}")
                .buildAndExpand(itemId)
                .toUriString();
        return delete(path, userId);
    }

    public ResponseEntity<Object> createComment(Long userId, Long itemId, CommentDto comment) {
        String path = UriComponentsBuilder.fromPath(API_PREFIX)
                .path("/{itemId}/comment")
                .buildAndExpand(itemId)
                .toUriString();
        return post(path, comment, userId);
    }
}