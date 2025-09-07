package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.InMemoryBookingRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.InMemoryItemRepository;
import ru.practicum.shareit.user.repository.InMemoryUserRepository;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final InMemoryBookingRepository bookingRepo;
    private final InMemoryItemRepository itemRepo;
    private final InMemoryUserRepository userRepo;
    private final BookingMapper bookingMapper; // инжектим бин

    @Override
    public BookingDto create(Long userId, BookingDto dto) {
        if (!userRepo.existsById(userId)) throw new NotFoundException("User not found: " + userId);
        if (dto == null) throw new BadRequestException("Booking data required");
        if (dto.getStart() == null || dto.getEnd() == null) throw new BadRequestException("Start and end required");
        if (!dto.getStart().isBefore(dto.getEnd())) throw new BadRequestException("Start must be before end");

        if (!itemRepo.existsById(dto.getItemId())) throw new NotFoundException("Item not found: " + dto.getItemId());
        Item item = itemRepo.findById(dto.getItemId()).get();

        if (Boolean.FALSE.equals(item.getAvailable())) throw new BadRequestException("Item is not available");
        if (item.getOwnerId().equals(userId)) throw new NotFoundException("Owner cannot book own item");

        Booking booking = bookingMapper.toModel(dto);
        booking.setBookerId(userId);
        booking.setStatus(BookingStatus.WAITING);

        booking = bookingRepo.save(booking);
        return bookingMapper.toDto(booking);
    }

    @Override
    public BookingDto approve(Long ownerId, Long bookingId, boolean approved) {
        Booking booking = bookingRepo.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found: " + bookingId));
        Item item = itemRepo.findById(booking.getItemId())
                .orElseThrow(() -> new NotFoundException("Item not found: " + booking.getItemId()));

        if (!item.getOwnerId().equals(ownerId)) throw new ForbiddenException("Only owner can approve/reject");
        if (booking.getStatus() != BookingStatus.WAITING) throw new BadRequestException("Only WAITING can be changed");

        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        bookingRepo.save(booking);

        return bookingMapper.toDto(booking);
    }

    @Override
    public BookingDto getById(Long userId, Long bookingId) {
        Booking booking = bookingRepo.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found: " + bookingId));
        Item item = itemRepo.findById(booking.getItemId())
                .orElseThrow(() -> new NotFoundException("Item not found: " + booking.getItemId()));

        if (!booking.getBookerId().equals(userId) && !item.getOwnerId().equals(userId)) {
            throw new NotFoundException("Booking not accessible");
        }
        return bookingMapper.toDto(booking);
    }
}
