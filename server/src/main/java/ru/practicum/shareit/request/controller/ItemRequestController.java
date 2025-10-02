package ru.practicum.shareit.request.controller;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.common.HeaderConstants;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

@RestController("itemRequestControllerBean")
@Validated
@RequestMapping("/requests")
public class ItemRequestController {
    private final ItemRequestService service;

    public ItemRequestController(ItemRequestService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ItemRequestDto> create(@RequestHeader(HeaderConstants.X_SHARER_USER_ID) Long userId,
                                                 @RequestBody ItemRequestDto dto) {
        return ResponseEntity.ok(service.create(userId, dto));
    }

    @GetMapping
    public ResponseEntity<List<ItemRequestDto>> getByRequester(@RequestHeader(HeaderConstants.X_SHARER_USER_ID) Long userId) {
        return ResponseEntity.ok(service.getByRequester(userId));
    }

    @GetMapping("/all")
    public ResponseEntity<List<ItemRequestDto>> getAll(@RequestHeader(HeaderConstants.X_SHARER_USER_ID) Long userId,
                                                       @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                                       @RequestParam(defaultValue = "10") @Positive int size) {
        return ResponseEntity.ok(service.getAll(userId, from, size));
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<ItemRequestDto> getById(@RequestHeader(HeaderConstants.X_SHARER_USER_ID) Long userId,
                                                  @PathVariable Long requestId) {
        return ResponseEntity.ok(service.getById(userId, requestId));
    }
}
