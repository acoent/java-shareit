package ru.practicum.shareit.booking.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;

@RestController("bookingControllerBean")
@RequestMapping("/bookings")
public class BookingController {
    private final BookingService service;
    public BookingController(BookingService service) { this.service = service; }

    @PostMapping
    public ResponseEntity<BookingDto> create(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @RequestBody BookingDto dto) {
        return ResponseEntity.ok(service.create(userId, dto));
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<BookingDto> approve(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                              @PathVariable Long bookingId,
                                              @RequestParam boolean approved) {
        return ResponseEntity.ok(service.approve(ownerId, bookingId, approved));
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingDto> get(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long bookingId) {
        return ResponseEntity.ok(service.getById(userId, bookingId));
    }
}
