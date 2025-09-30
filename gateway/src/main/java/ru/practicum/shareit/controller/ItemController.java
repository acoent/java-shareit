package ru.practicum.shareit.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.client.ItemClient;
import ru.practicum.shareit.common.HeaderConstants;
import ru.practicum.shareit.dto.CommentDto;
import ru.practicum.shareit.dto.ItemDto;

@RestController
@RequestMapping("/items")
@Validated
public class ItemController {
    private final ItemClient itemClient;

    public ItemController(ItemClient itemClient) {
        this.itemClient = itemClient;
    }

    @PostMapping
    public ResponseEntity<Object> createItem(@RequestHeader(HeaderConstants.X_SHARER_USER_ID) Long userId,
                                             @RequestBody @Valid ItemDto itemDto) {
        return itemClient.createItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestHeader(HeaderConstants.X_SHARER_USER_ID) Long userId,
                                             @PathVariable Long itemId,
                                             @RequestBody ItemDto itemDto) {
        return itemClient.updateItem(userId, itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItem(@RequestHeader(HeaderConstants.X_SHARER_USER_ID) Long userId,
                                          @PathVariable Long itemId) {
        return itemClient.getItem(userId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> getUserItems(@RequestHeader(HeaderConstants.X_SHARER_USER_ID) Long userId) {
        return itemClient.getUserItems(userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(@RequestHeader(HeaderConstants.X_SHARER_USER_ID) Long userId,
                                              @RequestParam String text,
                                              @RequestParam(defaultValue = "0") int from,
                                              @RequestParam(defaultValue = "10") int size) {
        return itemClient.searchItems(userId, text, from, size);
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Object> deleteItem(@RequestHeader(HeaderConstants.X_SHARER_USER_ID) Long userId,
                                             @PathVariable Long itemId) {
        return itemClient.deleteItem(userId, itemId);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@RequestHeader(HeaderConstants.X_SHARER_USER_ID) Long userId,
                                                @PathVariable Long itemId,
                                                @RequestBody @Valid CommentDto dto) {
        return itemClient.createComment(userId, itemId, dto);
    }
}