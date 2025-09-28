package ru.practicum.shareit.client;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.dto.BookingDto;

import java.time.LocalDateTime;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingClientTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private BookingClient bookingClient;

    @Test
    void createBooking_ShouldCallPostWithCorrectParameters() {
        Long userId = 1L;
        BookingDto bookingDto = BookingDto.builder()
                .itemId(1L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("created");

        BookingClient spyClient = spy(new BookingClient("http://localhost:8080"));
        doReturn(expectedResponse).when(spyClient).post(eq("/bookings"), eq(bookingDto), eq(userId));

        ResponseEntity<Object> result = spyClient.createBooking(userId, bookingDto);

        assertEquals(expectedResponse, result);
        verify(spyClient).post("/bookings", bookingDto, userId);
    }

    @Test
    void updateBooking_ShouldCallPatchWithCorrectParameters() {
        Long userId = 1L;
        Long bookingId = 2L;
        Boolean approved = true;
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("updated");

        BookingClient spyClient = spy(new BookingClient("http://localhost:8080"));
        doReturn(expectedResponse).when(spyClient).patch(anyString(), any(Map.class), eq(userId));

        ResponseEntity<Object> result = spyClient.updateBooking(userId, bookingId, approved);

        assertEquals(expectedResponse, result);
        verify(spyClient).patch(eq("/bookings/" + bookingId), argThat(body -> {
            Map<String, Object> bodyMap = (Map<String, Object>) body;
            return bodyMap.get("approved").equals(approved);
        }), eq(userId));
    }

    @Test
    void getBooking_ShouldCallGetWithCorrectParameters() {
        Long userId = 1L;
        Long bookingId = 2L;
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("booking");

        BookingClient spyClient = spy(new BookingClient("http://localhost:8080"));
        doReturn(expectedResponse).when(spyClient).get(anyString(), any(), eq(userId));

        ResponseEntity<Object> result = spyClient.getBooking(userId, bookingId);

        assertEquals(expectedResponse, result);
        verify(spyClient).get("/bookings/" + bookingId, null, userId);
    }

    @Test
    void getUserBookings_ShouldCallGetWithCorrectParameters() {
        Long userId = 1L;
        String state = "ALL";
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("bookings");

        BookingClient spyClient = spy(new BookingClient("http://localhost:8080"));
        doReturn(expectedResponse).when(spyClient).get(anyString(), any(Map.class), eq(userId));

        ResponseEntity<Object> result = spyClient.getUserBookings(userId, state);

        assertEquals(expectedResponse, result);
        verify(spyClient).get(eq("/bookings"), argThat(params -> {
            Map<String, Object> paramsMap = params;
            return paramsMap.get("state").equals(state);
        }), eq(userId));
    }

    @Test
    void getOwnerBookings_ShouldCallGetWithCorrectParameters() {
        Long userId = 1L;
        String state = "WAITING";
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("owner bookings");

        BookingClient spyClient = spy(new BookingClient("http://localhost:8080"));
        doReturn(expectedResponse).when(spyClient).get(anyString(), any(Map.class), eq(userId));

        ResponseEntity<Object> result = spyClient.getOwnerBookings(userId, state);

        assertEquals(expectedResponse, result);
        verify(spyClient).get(eq("/bookings/owner"), argThat(params -> {
            Map<String, Object> paramsMap = params;
            return paramsMap.get("state").equals(state);
        }), eq(userId));
    }

    @Test
    void constructor_ShouldCreateClientWithCorrectServerUrl() {
        String serverUrl = "http://test-server:8080";
        BookingClient client = new BookingClient(serverUrl);

        assertNotNull(client);
    }
}