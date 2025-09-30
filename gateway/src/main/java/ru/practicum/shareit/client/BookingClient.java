package ru.practicum.shareit.client;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.dto.BookingDto;

@Service
public class BookingClient extends BaseClient {

    public BookingClient(RestTemplate restTemplate) {
        super(restTemplate);
    }

    public ResponseEntity<Object> createBooking(Long userId, BookingDto bookingDto) {
        return post("/bookings", bookingDto, userId);
    }

    public ResponseEntity<Object> updateBooking(Long userId, Long bookingId, Boolean approved) {
        String path = "/bookings/" + bookingId + "?approved=" + approved;
        return patch(path, null, userId);
    }

    public ResponseEntity<Object> getBooking(Long userId, Long bookingId) {
        return get("/bookings/" + bookingId, null, userId);
    }

    public ResponseEntity<Object> getUserBookings(Long userId, String state, int from, int size) {
        String path = "/bookings?state=" + state + "&from=" + from + "&size=" + size;
        return get(path, null, userId);
    }

    public ResponseEntity<Object> getOwnerBookings(Long userId, String state, int from, int size) {
        String path = "/bookings/owner?state=" + state + "&from=" + from + "&size=" + size;
        return get(path, null, userId);
    }
}