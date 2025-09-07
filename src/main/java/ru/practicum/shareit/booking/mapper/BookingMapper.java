package ru.practicum.shareit.booking.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;

@Mapper(componentModel = "spring")
public interface BookingMapper {
    BookingMapper INSTANCE = Mappers.getMapper(BookingMapper.class);

    @Mapping(target = "status", expression = "java(b.getStatus() != null ? b.getStatus().name() : null)")
    BookingDto toDto(Booking b);

    @Mapping(target = "status", expression = "java(d.getStatus() != null ? ru.practicum.shareit.booking.model.BookingStatus.valueOf(d.getStatus()) : null)")
    Booking toModel(BookingDto d);
}
