package ru.practicum.shareit.booking.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.common.HeaderConstants;

import java.util.List;

@RestController("bookingControllerBean")
@Validated
@RequestMapping("/bookings")
public class BookingController {
    private final BookingService service;

    public BookingController(BookingService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<BookingResponseDto> create(@RequestHeader(HeaderConstants.X_SHARER_USER_ID) Long userId,
                                                     @RequestBody @Valid BookingDto dto) {
        BookingResponseDto created = service.create(userId, dto);
        return ResponseEntity.ok(created);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<BookingResponseDto> approve(@RequestHeader(HeaderConstants.X_SHARER_USER_ID) Long ownerId,
                                                      @PathVariable Long bookingId,
                                                      @RequestParam boolean approved) {
        BookingResponseDto updated = service.approve(ownerId, bookingId, approved);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingResponseDto> getById(@RequestHeader(HeaderConstants.X_SHARER_USER_ID) Long userId,
                                                      @PathVariable Long bookingId) {
        BookingResponseDto dto = service.getById(userId, bookingId);
        return ResponseEntity.ok(dto);
    }

    @GetMapping
    public ResponseEntity<List<BookingResponseDto>> getByBooker(
            @RequestHeader(HeaderConstants.X_SHARER_USER_ID) Long userId,
            @RequestParam(value = "state", required = false, defaultValue = "ALL") String state,
            @RequestParam(value = "from", required = false, defaultValue = "0") int from,
            @RequestParam(value = "size", required = false, defaultValue = "10") int size) {
        List<BookingResponseDto> list = service.getByBooker(userId, state, from, size);
        return ResponseEntity.ok(list);
    }

    @GetMapping("/owner")
    public ResponseEntity<List<BookingResponseDto>> getByOwner(
            @RequestHeader(HeaderConstants.X_SHARER_USER_ID) Long ownerId,
            @RequestParam(value = "state", required = false, defaultValue = "ALL") String state,
            @RequestParam(value = "from", required = false, defaultValue = "0") int from,
            @RequestParam(value = "size", required = false, defaultValue = "10") int size) {
        List<BookingResponseDto> list = service.getByOwner(ownerId, state, from, size);
        return ResponseEntity.ok(list);
    }
}
