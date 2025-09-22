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
 *
 * NOTE about EntityGraph:
 * We use {@link EntityGraph} on queries that return lists/pages of bookings to eagerly fetch
 * the associated item and booker in the same query. This prevents N+1 queries when the service
 * maps bookings to DTOs containing brief item/booker info (last/next booking, booker short).
 *
 * Each paged method below returns a Page<Booking> so pagination is applied by DB query,
 * avoiding loading unnecessary rows into memory.
 */
@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    /**
     * Fetch bookings for a booker with item and booker eagerly fetched to avoid N+1 when mapping to DTOs.
     */
    @EntityGraph(attributePaths = {"item", "booker"})
    Page<Booking> findByBooker_IdOrderByStartDesc(Long bookerId, Pageable pageable);

    @EntityGraph(attributePaths = {"item", "booker"})
    Page<Booking> findByBooker_IdAndStatusOrderByStartDesc(Long bookerId, BookingStatus status, Pageable pageable);

    /**
     * Find current bookings: start <= now && end >= now
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

    /*
     * Owner-side queries (by item.owner.id) with same EntityGraph rationale:
     * when owner requests bookings we also need item and booker data for DTOs.
     */
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

    /**
     * Helper methods used to compute last/next booking for an item.
     * These are single-row queries and also eager-load item & booker to avoid extra selects.
     */
    @EntityGraph(attributePaths = {"item", "booker"})
    Optional<Booking> findFirstByItem_IdAndStatusAndStartBeforeOrderByStartDesc(Long itemId,
                                                                                BookingStatus status,
                                                                                LocalDateTime before);

    @EntityGraph(attributePaths = {"item", "booker"})
    Optional<Booking> findFirstByItem_IdAndStatusAndStartAfterOrderByStartAsc(Long itemId,
                                                                              BookingStatus status,
                                                                              LocalDateTime after);

    /**
     * Used to check whether a user had an approved booking for an item that ended before a time.
     * This is used for comment posting validation.
     */
    @EntityGraph(attributePaths = {"item", "booker"})
    List<Booking> findByBooker_IdAndItem_IdAndStatusAndEndBefore(Long bookerId,
                                                                 Long itemId,
                                                                 BookingStatus status,
                                                                 LocalDateTime endBefore);
}
