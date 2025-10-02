package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    Page<Booking> findByBooker_IdOrderByStartDesc(Long bookerId, Pageable pageable);

    Page<Booking> findByBooker_IdAndStatusOrderByStartDesc(Long bookerId, BookingStatus status, Pageable pageable);

    Page<Booking> findByBooker_IdAndStartBeforeAndEndAfterOrderByStartDesc(Long bookerId,
                                                                           LocalDateTime before,
                                                                           LocalDateTime after,
                                                                           Pageable pageable);

    Page<Booking> findByBooker_IdAndEndBeforeOrderByStartDesc(Long bookerId, LocalDateTime before, Pageable pageable);

    Page<Booking> findByBooker_IdAndStartAfterOrderByStartDesc(Long bookerId, LocalDateTime after, Pageable pageable);

    Page<Booking> findByItem_Owner_IdOrderByStartDesc(Long ownerId, Pageable pageable);

    Page<Booking> findByItem_Owner_IdAndStatusOrderByStartDesc(Long ownerId, BookingStatus status, Pageable pageable);

    Page<Booking> findByItem_Owner_IdAndStartBeforeAndEndAfterOrderByStartDesc(Long ownerId,
                                                                               LocalDateTime before,
                                                                               LocalDateTime after,
                                                                               Pageable pageable);

    Page<Booking> findByItem_Owner_IdAndEndBeforeOrderByStartDesc(Long ownerId, LocalDateTime before, Pageable pageable);

    Page<Booking> findByItem_Owner_IdAndStartAfterOrderByStartDesc(Long ownerId, LocalDateTime after, Pageable pageable);

    Optional<Booking> findFirstByItem_IdAndStatusAndStartBeforeOrderByStartDesc(Long itemId,
                                                                                BookingStatus status,
                                                                                LocalDateTime before);

    Optional<Booking> findFirstByItem_IdAndStatusAndStartAfterOrderByStartAsc(Long itemId,
                                                                              BookingStatus status,
                                                                              LocalDateTime after);

    List<Booking> findByBooker_IdAndItem_IdAndStatusAndEndBefore(Long bookerId,
                                                                 Long itemId,
                                                                 BookingStatus status,
                                                                 LocalDateTime endBefore);
}
