package ru.practicum.shareit.client;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ru.practicum.shareit.dto.BookingDto;

@Service
public class BookingClient extends BaseClient {
    private static final String API_PREFIX = "/bookings";

    public BookingClient(RestTemplate restTemplate) {
        super(restTemplate);
    }

    public ResponseEntity<Object> createBooking(Long userId, BookingDto bookingDto) {
        return post(API_PREFIX, bookingDto, userId);
    }

    public ResponseEntity<Object> updateBooking(Long userId, Long bookingId, Boolean approved) {
        String path = UriComponentsBuilder.fromPath(API_PREFIX)
                .path("/{bookingId}")
                .queryParam("approved", approved)
                .buildAndExpand(bookingId)
                .toUriString();
        return patch(path, null, userId);
    }

    public ResponseEntity<Object> getBooking(Long userId, Long bookingId) {
        String path = UriComponentsBuilder.fromPath(API_PREFIX)
                .path("/{bookingId}")
                .buildAndExpand(bookingId)
                .toUriString();
        return get(path, null, userId);
    }

    public ResponseEntity<Object> getUserBookings(Long userId, String state, int from, int size) {
        String path = UriComponentsBuilder.fromPath(API_PREFIX)
                .queryParam("state", state)
                .queryParam("from", from)
                .queryParam("size", size)
                .toUriString();
        return get(path, null, userId);
    }

    public ResponseEntity<Object> getOwnerBookings(Long userId, String state, int from, int size) {
        String path = UriComponentsBuilder.fromPath(API_PREFIX)
                .path("/owner")
                .queryParam("state", state)
                .queryParam("from", from)
                .queryParam("size", size)
                .toUriString();
        return get(path, null, userId);
    }
}