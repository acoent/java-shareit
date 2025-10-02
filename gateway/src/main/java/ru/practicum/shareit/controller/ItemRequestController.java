package ru.practicum.shareit.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.client.ItemRequestClient;
import ru.practicum.shareit.common.HeaderConstants;
import ru.practicum.shareit.dto.ItemRequestDto;

@RestController
@RequestMapping("/requests")
@Validated
public class ItemRequestController {
    private final ItemRequestClient requestClient;

    public ItemRequestController(ItemRequestClient requestClient) {
        this.requestClient = requestClient;
    }

    @PostMapping
    public ResponseEntity<Object> createRequest(@RequestHeader(HeaderConstants.X_SHARER_USER_ID) Long userId,
                                                @RequestBody @Valid ItemRequestDto requestDto) {
        return requestClient.createRequest(userId, requestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getUserRequests(@RequestHeader(HeaderConstants.X_SHARER_USER_ID) Long userId) {
        return requestClient.getUserRequests(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequests(@RequestHeader(HeaderConstants.X_SHARER_USER_ID) Long userId,
                                                 @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                                 @RequestParam(defaultValue = "10") @Positive int size) {
        return requestClient.getAllRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequest(@RequestHeader(HeaderConstants.X_SHARER_USER_ID) Long userId,
                                             @PathVariable Long requestId) {
        return requestClient.getRequest(userId, requestId);
    }
}