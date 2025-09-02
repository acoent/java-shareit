package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.dto.CommentDto;

import java.util.List;

public interface ItemService {
    ItemDto create(Long userId, ItemDto itemDto);
    ItemDto update(Long userId, Long itemId, ItemDto itemDto);
    ItemResponseDto getById(Long userId, Long itemId);
    List<ItemResponseDto> getItemsByOwner(Long ownerId, int from, int size);
    List<ItemDto> search(String text, int from, int size);
    CommentDto addComment(Long userId, Long itemId, CommentDto commentDto);
}
