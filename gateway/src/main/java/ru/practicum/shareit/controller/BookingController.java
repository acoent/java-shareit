package ru.practicum.shareit.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.client.BookingClient;
import ru.practicum.shareit.dto.BookingDto;
import ru.practicum.shareit.common.HeaderConstants;

@RestController
@RequestMapping("/bookings")
public class BookingController {
    private final BookingClient bookingClient;

    public BookingController(BookingClient bookingClient) {
        this.bookingClient = bookingClient;
    }

    @PostMapping
    public ResponseEntity<Object> createBooking(@RequestHeader(HeaderConstants.X_SHARER_USER_ID) Long userId,
                                                @RequestBody BookingDto bookingDto) {
        return bookingClient.createBooking(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> updateBooking(@RequestHeader(HeaderConstants.X_SHARER_USER_ID) Long userId,
                                                @PathVariable Long bookingId,
                                                @RequestParam Boolean approved) {
        return bookingClient.updateBooking(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(@RequestHeader(HeaderConstants.X_SHARER_USER_ID) Long userId,
                                             @PathVariable Long bookingId) {
        return bookingClient.getBooking(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> getUserBookings(@RequestHeader(HeaderConstants.X_SHARER_USER_ID) Long userId,
                                                  @RequestParam(defaultValue = "ALL") String state,
                                                  @RequestParam(defaultValue = "0") int from,
                                                  @RequestParam(defaultValue = "10") int size) {
        return bookingClient.getUserBookings(userId, state, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getOwnerBookings(@RequestHeader(HeaderConstants.X_SHARER_USER_ID) Long userId,
                                                   @RequestParam(defaultValue = "ALL") String state,
                                                   @RequestParam(defaultValue = "0") int from,
                                                   @RequestParam(defaultValue = "10") int size) {
        return bookingClient.getOwnerBookings(userId, state, from, size);
    }
}
