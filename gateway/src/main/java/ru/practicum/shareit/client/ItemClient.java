package ru.practicum.shareit.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.dto.ItemDto;

import java.util.Map;

@Service
public class ItemClient extends BaseClient {

    public ItemClient(@Value("${shareit.server.url}") String serverUrl) {
        super(new org.springframework.web.client.RestTemplate(), serverUrl);
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

    public ResponseEntity<Object> searchItems(Long userId, String text) {
        Map<String, Object> parameters = Map.of("text", text);
        return get("/items/search", parameters, userId);
    }

    public ResponseEntity<Object> deleteItem(Long userId, Long itemId) {
        return delete("/items/" + itemId, userId);
    }
}
