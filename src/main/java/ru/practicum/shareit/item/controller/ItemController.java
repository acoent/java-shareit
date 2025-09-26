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
import ru.practicum.shareit.item.dto.ItemResponseDto;
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
        ItemDto created = service.create(userId, dto);
        return ResponseEntity.ok(created);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<ItemDto> update(@RequestHeader(HeaderConstants.X_SHARER_USER_ID) Long userId,
                                          @PathVariable Long itemId,
                                          @RequestBody ItemDto dto) {
        ItemDto updated = service.update(userId, itemId, dto);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<ItemResponseDto> getById(@RequestHeader(HeaderConstants.X_SHARER_USER_ID) Long userId,
                                                   @PathVariable Long itemId) {
        ItemResponseDto response = service.getById(userId, itemId);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<ItemResponseDto>> getByOwner(@RequestHeader(HeaderConstants.X_SHARER_USER_ID) Long userId,
                                                            @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                                            @RequestParam(defaultValue = "10") @Positive int size) {
        List<ItemResponseDto> items = service.getByOwner(userId, from, size);
        return ResponseEntity.ok(items);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ItemDto>> search(@RequestParam String text,
                                                @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                                @RequestParam(defaultValue = "10") @Positive int size) {
        List<ItemDto> result = service.search(text, from, size);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<CommentDto> addComment(@RequestHeader(HeaderConstants.X_SHARER_USER_ID) Long userId,
                                                 @PathVariable Long itemId,
                                                 @RequestBody @Valid CommentDto dto) {
        CommentDto created = service.addComment(userId, itemId, dto);
        return ResponseEntity.ok(created);
    }
}
