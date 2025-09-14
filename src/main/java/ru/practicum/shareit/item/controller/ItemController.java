package ru.practicum.shareit.item.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.common.HeaderConstants;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@RestController("itemControllerBean")
@Validated
@RequestMapping("/items")
public class ItemController {

    private final ItemService service;

    public ItemController(ItemService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ItemDto> create(
            @RequestHeader(HeaderConstants.X_SHARER_USER_ID) Long userId,
            @RequestBody @Valid ItemDto dto
    ) {
        return ResponseEntity.ok(service.create(userId, dto));
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<ItemDto> update(@RequestHeader(HeaderConstants.X_SHARER_USER_ID) Long userId,
                                          @PathVariable Long itemId,
                                          @RequestBody ItemDto dto) {
        return ResponseEntity.ok(service.update(userId, itemId, dto));
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<ItemDto> getById(@RequestHeader(HeaderConstants.X_SHARER_USER_ID) Long userId,
                                           @PathVariable Long itemId) {
        return ResponseEntity.ok(service.getById(userId, itemId));
    }

    @GetMapping
    public ResponseEntity<List<ItemDto>> getByOwner(@RequestHeader(HeaderConstants.X_SHARER_USER_ID) Long userId,
                                                    @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                                    @RequestParam(defaultValue = "10") @Positive int size) {
        return ResponseEntity.ok(service.getByOwner(userId, from, size));
    }

    @GetMapping("/search")
    public ResponseEntity<List<ItemDto>> search(@RequestParam String text,
                                                @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                                @RequestParam(defaultValue = "10") @Positive int size) {
        return ResponseEntity.ok(service.search(text, from, size));
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<CommentDto> addComment(@RequestHeader(HeaderConstants.X_SHARER_USER_ID) Long userId,
                                                 @PathVariable Long itemId,
                                                 @RequestBody @Valid CommentDto dto) {
        return ResponseEntity.ok(service.addComment(userId, itemId, dto));
    }
}
