package ru.practicum.shareit.booking.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
public class InMemoryBookingRepository {

    private final Map<Long, Booking> storage = new ConcurrentHashMap<>();
    private final AtomicLong seq = new AtomicLong(1);

    public Booking save(Booking b) {
        if (b.getId() == null) b.setId(seq.getAndIncrement());
        storage.put(b.getId(), b);
        return b;
    }

    public Optional<Booking> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    public List<Booking> findByBookerId(Long bookerId) {
        return storage.values().stream().filter(b -> Objects.equals(b.getBookerId(), bookerId))
                .sorted(Comparator.comparing(Booking::getStart).reversed()).collect(Collectors.toList());
    }

    public List<Booking> findByItemId(Long itemId) {
        return storage.values().stream().filter(b -> Objects.equals(b.getItemId(), itemId))
                .sorted(Comparator.comparing(Booking::getStart).reversed()).collect(Collectors.toList());
    }

    public List<Booking> findByBookerIdAndItemId(Long bookerId, Long itemId) {
        return storage.values().stream().filter(b -> Objects.equals(b.getBookerId(), bookerId) && Objects.equals(b.getItemId(), itemId))
                .collect(Collectors.toList());
    }

    public List<Booking> findFinishedApprovedByBookerAndItem(Long bookerId, Long itemId, LocalDateTime now) {
        return storage.values().stream()
                .filter(b -> Objects.equals(b.getBookerId(), bookerId) && Objects.equals(b.getItemId(), itemId))
                .filter(b -> b.getStatus() == BookingStatus.APPROVED)
                .filter(b -> b.getEnd() != null && !b.getEnd().isAfter(now))
                .collect(Collectors.toList());
    }
}
