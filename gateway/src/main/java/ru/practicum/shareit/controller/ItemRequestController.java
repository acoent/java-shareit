package ru.practicum.shareit.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.client.ItemRequestClient;
import ru.practicum.shareit.dto.ItemRequestDto;
import ru.practicum.shareit.common.HeaderConstants;

@RestController
@RequestMapping("/requests")
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;

    public ItemRequestController(ItemRequestClient itemRequestClient) {
        this.itemRequestClient = itemRequestClient;
    }

    @PostMapping
    public ResponseEntity<Object> createRequest(@RequestHeader(HeaderConstants.X_SHARER_USER_ID) Long userId,
                                                @RequestBody ItemRequestDto requestDto) {
        return itemRequestClient.createRequest(userId, requestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getUserRequests(@RequestHeader(HeaderConstants.X_SHARER_USER_ID) Long userId) {
        return itemRequestClient.getUserRequests(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequests(@RequestHeader(HeaderConstants.X_SHARER_USER_ID) Long userId,
                                                 @RequestParam(defaultValue = "0") int from,
                                                 @RequestParam(defaultValue = "10") int size) {
        return itemRequestClient.getAllRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequest(@RequestHeader(HeaderConstants.X_SHARER_USER_ID) Long userId,
                                             @PathVariable Long requestId) {
        return itemRequestClient.getRequest(userId, requestId);
    }
}
