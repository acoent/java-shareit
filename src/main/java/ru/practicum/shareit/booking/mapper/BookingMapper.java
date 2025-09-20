package ru.practicum.shareit.booking.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.user.dto.UserShortDto;

/**
 * MapStruct mapper for Booking <-> DTOs.
 * Uses componentModel = "spring" so Spring can inject mapper.
 * Default helper methods convert nested entities to short DTOs.
 */
@Mapper(componentModel = "spring")
public interface BookingMapper {

    BookingMapper INSTANCE = Mappers.getMapper(BookingMapper.class);

    // BookingDto -> Booking (service attaches item/booker/status)
    @Mapping(target = "item", ignore = true)
    @Mapping(target = "booker", ignore = true)
    @Mapping(target = "status", ignore = true)
    Booking toModel(BookingDto dto);

    // Booking -> BookingResponseDto (map nested item/booker to short DTOs)
    @Mapping(target = "item", qualifiedByName = "toItemShort")
    @Mapping(target = "booker", qualifiedByName = "toUserShort")
    BookingResponseDto toResponseDto(Booking booking);

    @Named("toItemShort")
    default ItemShortDto toItemShort(ru.practicum.shareit.item.model.Item item) {
        if (item == null) return null;
        return new ItemShortDto(item.getId(), item.getName());
    }

    @Named("toUserShort")
    default UserShortDto toUserShort(ru.practicum.shareit.user.model.User user) {
        if (user == null) return null;
        return new UserShortDto(user.getId(), user.getName());
    }
}
