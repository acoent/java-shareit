package ru.practicum.shareit.booking.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
public class InMemoryBookingRepository {
    private final Map<Long, Booking> bookings = new ConcurrentHashMap<>();
    private final AtomicLong nextId = new AtomicLong(1);

    public Booking save(Booking b) {
        if (b.getId() == null) b.setId(nextId.getAndIncrement());
        bookings.put(b.getId(), b);
        return b;
    }

    public Optional<Booking> findById(Long id) {
        return Optional.ofNullable(bookings.get(id));
    }

    public List<Booking> findByItemId(Long itemId) {
        return bookings.values().stream().filter(b -> b.getItemId().equals(itemId)).collect(Collectors.toList());
    }

    public List<Booking> findByItemIdAndStatus(Long itemId, BookingStatus status) {
        return bookings.values().stream().filter(b -> b.getItemId().equals(itemId) && b.getStatus() == status).collect(Collectors.toList());
    }

    public List<Booking> findByBookerIdAndItemId(Long bookerId, Long itemId) {
        return bookings.values().stream().filter(b -> b.getBookerId().equals(bookerId) && b.getItemId().equals(itemId)).collect(Collectors.toList());
    }
}
