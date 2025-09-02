package ru.practicum.shareit.request.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

@RestController("itemRequestControllerBean")
@RequestMapping("/requests")
public class ItemRequestController {
    private final ItemRequestService service;

    public ItemRequestController(ItemRequestService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ItemRequestDto> create(@RequestHeader("X-Sharer-User-Id") Long userId, @RequestBody ItemRequestDto dto) {
        return ResponseEntity.ok(service.create(userId, dto));
    }

    @GetMapping
    public ResponseEntity<List<ItemRequestDto>> getOwn(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return ResponseEntity.ok(service.getOwn(userId));
    }
}
