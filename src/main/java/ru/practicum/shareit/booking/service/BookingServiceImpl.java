package ru.practicum.shareit.booking.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.InMemoryBookingRepository;
import ru.practicum.shareit.item.repository.InMemoryItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.repository.InMemoryUserRepository;
import ru.practicum.shareit.exception.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

@Service
public class BookingServiceImpl implements BookingService {

    private final InMemoryBookingRepository bookingRepo;
    private final InMemoryItemRepository itemRepo;
    private final InMemoryUserRepository userRepo;
    private final ConcurrentMap<Long, Object> locks = new ConcurrentHashMap<>();

    public BookingServiceImpl(InMemoryBookingRepository bookingRepo, InMemoryItemRepository itemRepo, InMemoryUserRepository userRepo) {
        this.bookingRepo = bookingRepo;
        this.itemRepo = itemRepo;
        this.userRepo = userRepo;
    }

    @Override
    public BookingDto create(Long userId, BookingDto dto) {
        if (userId == null || !userRepo.existsById(userId)) throw new NotFoundException("User not found: " + userId);
        if (dto == null || dto.getItemId() == null) throw new BadRequestException("Booking must specify itemId");
        Item item = itemRepo.findById(dto.getItemId()).orElseThrow(() -> new NotFoundException("Item not found: " + dto.getItemId()));
        if (!Boolean.TRUE.equals(item.getAvailable()))
            throw new BadRequestException("Item is not available for booking");
        if (item.getOwnerId().equals(userId)) throw new NotFoundException("Owner cannot book own item");

        if (dto.getStart() == null || dto.getEnd() == null)
            throw new BadRequestException("Start and end must be provided");
        if (!dto.getStart().isBefore(dto.getEnd())) throw new BadRequestException("Start must be before end");
        if (dto.getEnd().isBefore(LocalDateTime.now())) throw new BadRequestException("End must be in the future");

        Booking b = BookingMapper.toModel(dto);
        b.setBookerId(userId);
        b.setStatus(BookingStatus.WAITING);

        Object lock = locks.computeIfAbsent(item.getId(), k -> new Object());
        synchronized (lock) {
            List<Booking> approved = bookingRepo.findByItemIdAndStatus(item.getId(), BookingStatus.APPROVED);
            boolean conflict = approved.stream().anyMatch(a -> overlap(a.getStart(), a.getEnd(), b.getStart(), b.getEnd()));
            if (conflict) throw new BadRequestException("Booking dates conflict with existing approved booking");
            Booking saved = bookingRepo.save(b);
            return BookingMapper.toDto(saved);
        }
    }

    @Override
    public BookingDto approve(Long ownerId, Long bookingId, boolean approved) {
        Booking booking = bookingRepo.findById(bookingId).orElseThrow(() -> new NotFoundException("Booking not found: " + bookingId));
        Item item = itemRepo.findById(booking.getItemId()).orElseThrow(() -> new NotFoundException("Item not found: " + booking.getItemId()));
        if (!item.getOwnerId().equals(ownerId)) throw new ForbiddenException("Only owner can approve bookings");

        Object lock = locks.computeIfAbsent(item.getId(), k -> new Object());
        synchronized (lock) {
            if (approved) {
                List<Booking> approvedBookings = bookingRepo.findByItemIdAndStatus(item.getId(), BookingStatus.APPROVED);
                boolean conflict = approvedBookings.stream().anyMatch(a -> overlap(a.getStart(), a.getEnd(), booking.getStart(), booking.getEnd()));
                if (conflict) throw new BadRequestException("Cannot approve: conflict with existing approved booking");
                booking.setStatus(BookingStatus.APPROVED);
            } else {
                booking.setStatus(BookingStatus.REJECTED);
            }
            bookingRepo.save(booking);
            return BookingMapper.toDto(booking);
        }
    }

    @Override
    public BookingDto getById(Long userId, Long bookingId) {
        Booking b = bookingRepo.findById(bookingId).orElseThrow(() -> new NotFoundException("Booking not found: " + bookingId));
        Item item = itemRepo.findById(b.getItemId()).orElseThrow(() -> new NotFoundException("Item not found: " + b.getItemId()));
        if (!b.getBookerId().equals(userId) && !item.getOwnerId().equals(userId))
            throw new ForbiddenException("Not authorized to view booking");
        return BookingMapper.toDto(b);
    }

    @Override
    public List<BookingDto> getBookingsForUser(Long userId) {
        return bookingRepo.findByBookerIdAndItemId(userId, null).stream().map(BookingMapper::toDto).collect(Collectors.toList());
    }

    private boolean overlap(LocalDateTime aStart, LocalDateTime aEnd, LocalDateTime bStart, LocalDateTime bEnd) {
        return !aEnd.isBefore(bStart) && !bEnd.isBefore(aStart);
    }
}
