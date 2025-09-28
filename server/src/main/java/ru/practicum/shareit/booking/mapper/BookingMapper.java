package ru.practicum.shareit.booking.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemShortDto;
import ru.practicum.shareit.user.dto.UserShortDto;


@Mapper(componentModel = "spring")
public interface BookingMapper {

    @Mapping(target = "item", ignore = true)
    @Mapping(target = "booker", ignore = true)
    @Mapping(target = "status", ignore = true)
    Booking toModel(BookingDto dto);

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
