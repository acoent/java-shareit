package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.dto.BookingShortDto;
import ru.practicum.shareit.item.dto.CommentDto;

import java.util.List;

public class ItemMapper {
    public static Item toModel(ItemDto dto, Long ownerId) {
        if (dto == null) return null;
        Item i = new Item();
        i.setId(dto.getId());
        i.setName(dto.getName());
        i.setDescription(dto.getDescription());
        i.setAvailable(dto.getAvailable());
        i.setOwnerId(ownerId);
        i.setRequestId(dto.getRequestId());
        return i;
    }

    public static ItemDto toDto(Item i) {
        if (i == null) return null;
        ItemDto d = new ItemDto();
        d.setId(i.getId());
        d.setName(i.getName());
        d.setDescription(i.getDescription());
        d.setAvailable(i.getAvailable());
        d.setRequestId(i.getRequestId());
        return d;
    }

    public static ItemResponseDto toResponse(Item i, BookingShortDto last, BookingShortDto next, List<CommentDto> comments) {
        ItemResponseDto r = new ItemResponseDto();
        r.setId(i.getId());
        r.setName(i.getName());
        r.setDescription(i.getDescription());
        r.setAvailable(i.getAvailable());
        r.setLastBooking(last);
        r.setNextBooking(next);
        r.setComments(comments);
        return r;
    }
}
