package ru.practicum.shareit.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.dto.ItemDto;

@Service
public class ItemClient extends BaseClient {

    public ItemClient(RestTemplate restTemplate, @Value("${shareit.server.url}") String serverUrl) {
        super(restTemplate, serverUrl);
    }

    public ResponseEntity<Object> createItem(Long userId, ItemDto itemDto) {
        return post("/items", itemDto, userId);
    }

    public ResponseEntity<Object> updateItem(Long userId, Long itemId, ItemDto itemDto) {
        return patch("/items/" + itemId, itemDto, userId);
    }

    public ResponseEntity<Object> getItem(Long userId, Long itemId) {
        return get("/items/" + itemId, null, userId);
    }

    public ResponseEntity<Object> getUserItems(Long userId) {
        return get("/items", null, userId);
    }

    public ResponseEntity<Object> searchItems(Long userId, String text, int from, int size) {
        String safeText = text == null ? "" : text;
        String path = "/items/search?text=" + safeText + "&from=" + from + "&size=" + size;
        return get(path, null, userId);
    }

    public ResponseEntity<Object> deleteItem(Long userId, Long itemId) {
        return delete("/items/" + itemId, userId);
    }

    public ResponseEntity<Object> createComment(Long userId, Long itemId, Object comment) {
        return post("/items/" + itemId + "/comment", comment, userId);
    }
}
