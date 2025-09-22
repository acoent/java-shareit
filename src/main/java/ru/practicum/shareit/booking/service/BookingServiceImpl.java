package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
import java.util.List;
import java.util.stream.Collectors;

/**
 * Booking service implementation.
 *
 * Responsibilities split:
 *  - Field-level validation (NotNull, Future, StartBeforeEnd) is performed on DTO level in controller (@Valid).
 *  - Service performs business validations: entity existence, ownership rules, availability, booking state changes.
 */
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

        // Fetch booker — this both validates existence and returns entity (saves extra existsById + find call).
        User booker = userRepo.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found: " + userId));

        // Fetch item
        Item item = itemRepo.findById(dto.getItemId())
                .orElseThrow(() -> new NotFoundException("Item not found: " + dto.getItemId()));

        // Business rule: owner cannot book own item.
        // This is business logic, so it belongs to the service layer (not controller).
        if (item.getOwner() != null && item.getOwner().getId().equals(userId)) {
            throw new NotFoundException("Owner cannot book own item");
        }

        // Field validations (null, order, future) are handled by @Valid on BookingDto in controller.
        // Service focuses on business constraints: availability, state, existence.

        if (Boolean.FALSE.equals(item.getAvailable())) {
            throw new BadRequestException("Item is not available");
        }

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

        if (from < 0) from = 0;
        if (size <= 0) throw new BadRequestException("size must be positive");
        int page = from / size;
        PageRequest pageRequest = PageRequest.of(page, size);

        Page<Booking> pageResult;
        LocalDateTime now = LocalDateTime.now();

        if (state == null || state.isBlank() || "ALL".equalsIgnoreCase(state)) {
            pageResult = bookingRepo.findByBooker_IdOrderByStartDesc(bookerId, pageRequest);
        } else {
            switch (state.toUpperCase()) {
                case "CURRENT":
                    pageResult = bookingRepo.findByBooker_IdAndStartBeforeAndEndAfterOrderByStartDesc(bookerId, now, now, pageRequest);
                    break;
                case "PAST":
                    pageResult = bookingRepo.findByBooker_IdAndEndBeforeOrderByStartDesc(bookerId, now, pageRequest);
                    break;
                case "FUTURE":
                    pageResult = bookingRepo.findByBooker_IdAndStartAfterOrderByStartDesc(bookerId, now, pageRequest);
                    break;
                case "WAITING":
                    pageResult = bookingRepo.findByBooker_IdAndStatusOrderByStartDesc(bookerId, BookingStatus.WAITING, pageRequest);
                    break;
                case "REJECTED":
                    pageResult = bookingRepo.findByBooker_IdAndStatusOrderByStartDesc(bookerId, BookingStatus.REJECTED, pageRequest);
                    break;
                default:
                    throw new BadRequestException("Unknown state: " + state);
            }
        }

        return pageResult.getContent().stream().map(bookingMapper::toResponseDto).collect(Collectors.toList());
    }

    @Override
    public List<BookingResponseDto> getByOwner(Long ownerId, String state, int from, int size) {
        if (!userRepo.existsById(ownerId)) throw new NotFoundException("User not found: " + ownerId);

        if (from < 0) from = 0;
        if (size <= 0) throw new BadRequestException("size must be positive");
        int page = from / size;
        PageRequest pageRequest = PageRequest.of(page, size);

        Page<Booking> pageResult;
        LocalDateTime now = LocalDateTime.now();

        if (state == null || state.isBlank() || "ALL".equalsIgnoreCase(state)) {
            pageResult = bookingRepo.findByItem_Owner_IdOrderByStartDesc(ownerId, pageRequest);
        } else {
            switch (state.toUpperCase()) {
                case "CURRENT":
                    pageResult = bookingRepo.findByItem_Owner_IdAndStartBeforeAndEndAfterOrderByStartDesc(ownerId, now, now, pageRequest);
                    break;
                case "PAST":
                    pageResult = bookingRepo.findByItem_Owner_IdAndEndBeforeOrderByStartDesc(ownerId, now, pageRequest);
                    break;
                case "FUTURE":
                    pageResult = bookingRepo.findByItem_Owner_IdAndStartAfterOrderByStartDesc(ownerId, now, pageRequest);
                    break;
                case "WAITING":
                    pageResult = bookingRepo.findByItem_Owner_IdAndStatusOrderByStartDesc(ownerId, BookingStatus.WAITING, pageRequest);
                    break;
                case "REJECTED":
                    pageResult = bookingRepo.findByItem_Owner_IdAndStatusOrderByStartDesc(ownerId, BookingStatus.REJECTED, pageRequest);
                    break;
                default:
                    throw new BadRequestException("Unknown state: " + state);
            }
        }

        return pageResult.getContent().stream().map(bookingMapper::toResponseDto).collect(Collectors.toList());
    }
}
