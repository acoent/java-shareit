package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepo;
    private final ItemRepository itemRepo;
    private final UserRepository userRepo;
    private final BookingMapper bookingMapper;

    @Override
    @Transactional
    public BookingResponseDto create(Long userId, BookingDto dto) {
        if (dto == null) throw new BadRequestException("Booking data must be provided");

        if (!userRepo.existsById(userId)) {
            throw new NotFoundException("User not found: " + userId);
        }
        if (dto.getItemId() == null) {
            throw new BadRequestException("itemId must be provided");
        }
        
        Item item = itemRepo.findById(dto.getItemId())
                .orElseThrow(() -> new NotFoundException("Item not found: " + dto.getItemId()));

        if (item.getOwner() != null && item.getOwner().getId().equals(userId)) {
            throw new NotFoundException("Owner cannot book own item");
        }
        if (dto.getStart() == null || dto.getEnd() == null) {
            throw new BadRequestException("start and end must be provided");
        }

        if (!dto.getStart().isBefore(dto.getEnd())) {
            throw new BadRequestException("start must be before end");
        }

        LocalDateTime now = LocalDateTime.now();
        if (dto.getStart().isBefore(now) || dto.getEnd().isBefore(now)) {
            throw new BadRequestException("start and end must be in the future");
        }

        if (Boolean.FALSE.equals(item.getAvailable())) {
            throw new BadRequestException("Item is not available");
        }

        User booker = userRepo.findById(userId).orElseThrow(() -> new NotFoundException("User not found: " + userId));

        Booking booking = bookingMapper.toModel(dto);
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.WAITING);

        booking = bookingRepo.save(booking);

        return bookingMapper.toResponseDto(booking);
    }

    @Override
    @Transactional
    public BookingResponseDto approve(Long ownerId, Long bookingId, boolean approved) {
        Booking booking = bookingRepo.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found: " + bookingId));
        Item item = booking.getItem();
        if (item == null) {
            throw new NotFoundException("Item not found for booking: " + bookingId);
        }

        if (!item.getOwner().getId().equals(ownerId)) {
            throw new ForbiddenException("Only owner can approve/reject");
        }
        if (booking.getStatus() != BookingStatus.WAITING) {
            throw new BadRequestException("Only WAITING can be changed");
        }

        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        booking = bookingRepo.save(booking);

        return bookingMapper.toResponseDto(booking);
    }

    @Override
    public BookingResponseDto getById(Long userId, Long bookingId) {
        Booking booking = bookingRepo.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found: " + bookingId));
        Item item = booking.getItem();
        if (!booking.getBooker().getId().equals(userId) && !item.getOwner().getId().equals(userId)) {
            throw new NotFoundException("Booking not accessible");
        }
        return bookingMapper.toResponseDto(booking);
    }

    @Override
    public List<BookingResponseDto> getByBooker(Long bookerId, String state, int from, int size) {
        if (!userRepo.existsById(bookerId)) throw new NotFoundException("User not found: " + bookerId);

        List<Booking> bookings = bookingRepo.findByBooker_IdOrderByStartDesc(bookerId);
        bookings = filterByState(bookings, state);
        return paginateAndMap(bookings, from, size);
    }

    @Override
    public List<BookingResponseDto> getByOwner(Long ownerId, String state, int from, int size) {
        if (!userRepo.existsById(ownerId)) throw new NotFoundException("User not found: " + ownerId);

        List<Booking> bookings = bookingRepo.findByItem_Owner_IdOrderByStartDesc(ownerId);
        bookings = filterByState(bookings, state);
        return paginateAndMap(bookings, from, size);
    }

    private List<Booking> filterByState(List<Booking> bookings, String state) {
        if (state == null || state.isBlank() || "ALL".equalsIgnoreCase(state)) {
            return bookings;
        }
        LocalDateTime now = LocalDateTime.now();
        switch (state.toUpperCase()) {
            case "CURRENT":
                return bookings.stream()
                        .filter(b -> !b.getStart().isAfter(now) && !b.getEnd().isBefore(now))
                        .collect(Collectors.toList());
            case "PAST":
                return bookings.stream()
                        .filter(b -> b.getEnd().isBefore(now))
                        .collect(Collectors.toList());
            case "FUTURE":
                return bookings.stream()
                        .filter(b -> b.getStart().isAfter(now))
                        .collect(Collectors.toList());
            case "WAITING":
                return bookings.stream()
                        .filter(b -> b.getStatus() == BookingStatus.WAITING)
                        .collect(Collectors.toList());
            case "REJECTED":
                return bookings.stream()
                        .filter(b -> b.getStatus() == BookingStatus.REJECTED)
                        .collect(Collectors.toList());
            default:
                throw new BadRequestException("Unknown state: " + state);
        }
    }

    private java.util.List<BookingResponseDto> paginateAndMap(List<Booking> bookings, int from, int size) {
        if (from < 0) from = 0;
        if (size <= 0) size = 10;
        return bookings.stream()
                .sorted(Comparator.comparing(Booking::getStart).reversed())
                .skip(from)
                .limit(size)
                .map(bookingMapper::toResponseDto)
                .collect(Collectors.toList());
    }
}
