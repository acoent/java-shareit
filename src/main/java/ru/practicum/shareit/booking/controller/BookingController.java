package ru.practicum.shareit.booking.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.common.HeaderConstants;

@RestController("bookingControllerBean")
@Validated
@RequestMapping("/bookings")
public class BookingController {
    private final BookingService service;

    public BookingController(BookingService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<BookingDto> create(@RequestHeader(HeaderConstants.X_SHARER_USER_ID) Long userId,
                                             @RequestBody @Valid BookingDto dto) {
        return ResponseEntity.ok(service.create(userId, dto));
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<BookingDto> approve(@RequestHeader(HeaderConstants.X_SHARER_USER_ID) Long ownerId,
                                              @PathVariable Long bookingId,
                                              @RequestParam boolean approved) {
        return ResponseEntity.ok(service.approve(ownerId, bookingId, approved));
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingDto> getById(@RequestHeader(HeaderConstants.X_SHARER_USER_ID) Long userId,
                                              @PathVariable Long bookingId) {
        return ResponseEntity.ok(service.getById(userId, bookingId));
    }
}
