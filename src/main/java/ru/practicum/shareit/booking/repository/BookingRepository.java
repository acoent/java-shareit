package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository for bookings.
 * EntityGraph used to eagerly fetch item & booker to avoid N+1 when mapping DTOs.
 */
@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    @EntityGraph(attributePaths = {"item", "booker"})
    Page<Booking> findByBooker_IdOrderByStartDesc(Long bookerId, Pageable pageable);

    @EntityGraph(attributePaths = {"item", "booker"})
    Page<Booking> findByBooker_IdAndStatusOrderByStartDesc(Long bookerId, BookingStatus status, Pageable pageable);

    /**
     * For CURRENT state: start <= now && end >= now
     */
    @EntityGraph(attributePaths = {"item", "booker"})
    Page<Booking> findByBooker_IdAndStartBeforeAndEndAfterOrderByStartDesc(Long bookerId,
                                                                           LocalDateTime before,
                                                                           LocalDateTime after,
                                                                           Pageable pageable);

    @EntityGraph(attributePaths = {"item", "booker"})
    Page<Booking> findByBooker_IdAndEndBeforeOrderByStartDesc(Long bookerId, LocalDateTime before, Pageable pageable);

    @EntityGraph(attributePaths = {"item", "booker"})
    Page<Booking> findByBooker_IdAndStartAfterOrderByStartDesc(Long bookerId, LocalDateTime after, Pageable pageable);

    @EntityGraph(attributePaths = {"item", "booker"})
    Page<Booking> findByItem_Owner_IdOrderByStartDesc(Long ownerId, Pageable pageable);

    @EntityGraph(attributePaths = {"item", "booker"})
    Page<Booking> findByItem_Owner_IdAndStatusOrderByStartDesc(Long ownerId, BookingStatus status, Pageable pageable);

    @EntityGraph(attributePaths = {"item", "booker"})
    Page<Booking> findByItem_Owner_IdAndStartBeforeAndEndAfterOrderByStartDesc(Long ownerId,
                                                                               LocalDateTime before,
                                                                               LocalDateTime after,
                                                                               Pageable pageable);

    @EntityGraph(attributePaths = {"item", "booker"})
    Page<Booking> findByItem_Owner_IdAndEndBeforeOrderByStartDesc(Long ownerId, LocalDateTime before, Pageable pageable);

    @EntityGraph(attributePaths = {"item", "booker"})
    Page<Booking> findByItem_Owner_IdAndStartAfterOrderByStartDesc(Long ownerId, LocalDateTime after, Pageable pageable);

    @EntityGraph(attributePaths = {"item", "booker"})
    Optional<Booking> findFirstByItem_IdAndStatusAndStartBeforeOrderByStartDesc(Long itemId,
                                                                                BookingStatus status,
                                                                                LocalDateTime before);

    @EntityGraph(attributePaths = {"item", "booker"})
    Optional<Booking> findFirstByItem_IdAndStatusAndStartAfterOrderByStartAsc(Long itemId,
                                                                              BookingStatus status,
                                                                              LocalDateTime after);

    @EntityGraph(attributePaths = {"item", "booker"})
    List<Booking> findByBooker_IdAndItem_IdAndStatusAndEndBefore(Long bookerId,
                                                                 Long itemId,
                                                                 BookingStatus status,
                                                                 LocalDateTime endBefore);
}
