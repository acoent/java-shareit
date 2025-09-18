package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;

import java.util.List;

public interface ItemService {
    ItemDto create(Long ownerId, ItemDto dto);

    ItemDto update(Long ownerId, Long itemId, ItemDto dto);

    ItemResponseDto getById(Long requesterId, Long itemId);

    List<ItemResponseDto> getByOwner(Long ownerId, int from, int size);

    List<ItemDto> search(String text, int from, int size);

    CommentDto addComment(Long authorId, Long itemId, CommentDto dto);
}
