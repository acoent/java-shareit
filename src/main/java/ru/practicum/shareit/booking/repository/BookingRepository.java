package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    @EntityGraph(attributePaths = {"item", "booker"})
    List<Booking> findByBooker_IdOrderByStartDesc(Long bookerId);

    @EntityGraph(attributePaths = {"item", "booker"})
    List<Booking> findByItem_IdOrderByStartDesc(Long itemId);

    @EntityGraph(attributePaths = {"item", "booker"})
    List<Booking> findByItem_Owner_IdOrderByStartDesc(Long ownerId);

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
