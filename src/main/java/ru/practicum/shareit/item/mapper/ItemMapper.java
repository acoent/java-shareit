package ru.practicum.shareit.item.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.BookingShortDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ItemMapper {

    public ItemDto toDto(Item i) {
        if (i == null) return null;
        ItemDto dto = new ItemDto();
        dto.setId(i.getId());
        dto.setName(i.getName());
        dto.setDescription(i.getDescription());
        dto.setAvailable(i.getAvailable());
        dto.setRequestId(i.getRequestId());
        if (i.getOwner() != null) dto.setOwnerId(i.getOwner().getId());
        return dto;
    }

    public Item toModel(ItemDto d) {
        if (d == null) return null;
        Item i = Item.builder()
                .id(d.getId())
                .name(d.getName())
                .description(d.getDescription())
                .available(d.getAvailable() != null ? d.getAvailable() : Boolean.TRUE)
                .requestId(d.getRequestId())
                .build();
        return i;
    }

    /**
     * Возвращаем ItemResponseDto. Комментарии типизированы как List<CommentDto>.
     */
    public ItemResponseDto toResponseDto(Item i, BookingShortDto last, BookingShortDto next, List<CommentDto> comments) {
        ItemResponseDto r = new ItemResponseDto();
        r.setId(i.getId());
        r.setName(i.getName());
        r.setDescription(i.getDescription());
        r.setAvailable(i.getAvailable());
        r.setRequestId(i.getRequestId());
        r.setComments(comments);
        r.setLastBooking(last);
        r.setNextBooking(next);
        return r;
    }

    public java.util.List<ItemDto> toDtoList(java.util.List<Item> items) {
        return items.stream().map(this::toDto).collect(Collectors.toList());
    }
}
