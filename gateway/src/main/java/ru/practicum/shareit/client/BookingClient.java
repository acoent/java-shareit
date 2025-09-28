package ru.practicum.shareit.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.dto.BookingDto;

import java.util.Map;

@Service
public class BookingClient extends BaseClient {

    public BookingClient(@Value("${shareit.server.url}") String serverUrl) {
        super(new org.springframework.web.client.RestTemplate(), serverUrl);
    }

    public ResponseEntity<Object> createBooking(Long userId, BookingDto bookingDto) {
        return post("/bookings", bookingDto, userId);
    }

    public ResponseEntity<Object> updateBooking(Long userId, Long bookingId, Boolean approved) {
        Map<String, Object> body = Map.of("approved", approved);
        return patch("/bookings/" + bookingId, body, userId);
    }

    public ResponseEntity<Object> getBooking(Long userId, Long bookingId) {
        return get("/bookings/" + bookingId, null, userId);
    }

    public ResponseEntity<Object> getUserBookings(Long userId, String state) {
        Map<String, Object> parameters = Map.of("state", state);
        return get("/bookings", parameters, userId);
    }

    public ResponseEntity<Object> getOwnerBookings(Long userId, String state) {
        Map<String, Object> parameters = Map.of("state", state);
        return get("/bookings/owner", parameters, userId);
    }
}
