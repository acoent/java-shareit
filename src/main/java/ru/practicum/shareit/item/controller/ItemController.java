package ru.practicum.shareit.item.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@RestController("itemControllerBean")
@RequestMapping("/items")
public class ItemController {
    private final ItemService service;

    public ItemController(ItemService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ItemDto> create(@RequestHeader("X-Sharer-User-Id") Long userId, @RequestBody ItemDto itemDto) {
        return ResponseEntity.ok(service.create(userId, itemDto));
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<ItemDto> update(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long itemId, @RequestBody ItemDto itemDto) {
        return ResponseEntity.ok(service.update(userId, itemId, itemDto));
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<ItemResponseDto> getById(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long itemId) {
        return ResponseEntity.ok(service.getById(userId, itemId));
    }

    @GetMapping
    public ResponseEntity<List<ItemResponseDto>> getOwnerItems(@RequestHeader("X-Sharer-User-Id") Long userId, @RequestParam(defaultValue = "0") int from, @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(service.getItemsByOwner(userId, from, size));
    }

    @GetMapping("/search")
    public ResponseEntity<List<ItemDto>> search(@RequestParam String text, @RequestParam(defaultValue = "0") int from, @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(service.search(text, from, size));
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<CommentDto> addComment(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long itemId, @RequestBody CommentDto commentDto) {
        return ResponseEntity.ok(service.addComment(userId, itemId, commentDto));
    }
}
