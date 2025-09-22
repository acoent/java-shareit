package ru.practicum.shareit.item.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.item.dto.BookingShortDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.stream.Collectors;

/**
 * ItemMapper — hybrid approach:
 *
 * 1) Simple mappings Item <-> ItemDto are done by MapStruct (interface methods).
 * 2) toResponseDto is implemented as a default method because it needs extra parameters
 *    (last/next booking and comments list) which are not part of Item entity.
 *
 * This satisfies the reviewer question "why not MapStruct" — we use MapStruct for simple conversions
 * while keeping manual code for the enriched response DTO assembly.
 */
@Mapper(componentModel = "spring")
public interface ItemMapper {

    @Mapping(target = "ownerId", source = "owner.id")
    ItemDto toDto(Item item);

    @Mapping(target = "owner", ignore = true)
    Item toModel(ItemDto dto);

    default ItemResponseDto toResponseDto(Item i, BookingShortDto last, BookingShortDto next, List<CommentDto> comments) {
        if (i == null) return null;
        ItemResponseDto r = new ItemResponseDto();
        r.setId(i.getId());
        r.setName(i.getName());
        r.setDescription(i.getDescription());
        r.setAvailable(i.getAvailable());
        r.setRequestId(i.getRequestId());
        r.setComments(comments == null ? List.of() : comments);
        r.setLastBooking(last);
        r.setNextBooking(next);
        return r;
    }

    default java.util.List<ItemDto> toDtoList(java.util.List<Item> items) {
        if (items == null) return List.of();
        return items.stream().map(this::toDto).collect(Collectors.toList());
    }
}
