package ru.practicum.shareit.request.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;

@Mapper(componentModel = "spring")
public interface ItemRequestMapper {

    @Mapping(target = "items", ignore = true)
    ItemRequestDto toDto(ItemRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "requesterId", ignore = true)
    @Mapping(target = "created", ignore = true)
    ItemRequest toModel(ItemRequestDto dto);
}