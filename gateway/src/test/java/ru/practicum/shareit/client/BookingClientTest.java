package ru.practicum.shareit.client;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.dto.BookingDto;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingClientTest {

    @Mock
    private RestTemplate restTemplate;

    @Test
    void createBooking_ShouldCallPostWithCorrectParameters() {
        Long userId = 1L;
        BookingDto bookingDto = BookingDto.builder()
                .itemId(1L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("created");

        BookingClient spyClient = spy(new BookingClient(restTemplate));
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

        BookingClient spyClient = spy(new BookingClient(restTemplate));
        String expectedPath = "/bookings/" + bookingId + "?approved=" + approved;
        doReturn(expectedResponse).when(spyClient).patch(eq(expectedPath), isNull(), eq(userId));

        ResponseEntity<Object> result = spyClient.updateBooking(userId, bookingId, approved);

        assertEquals(expectedResponse, result);
        verify(spyClient).patch(expectedPath, null, userId);
    }

    @Test
    void getBooking_ShouldCallGetWithCorrectParameters() {
        Long userId = 1L;
        Long bookingId = 2L;
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("booking");

        BookingClient spyClient = spy(new BookingClient(restTemplate));
        doReturn(expectedResponse).when(spyClient).get(anyString(), any(), eq(userId));

        ResponseEntity<Object> result = spyClient.getBooking(userId, bookingId);

        assertEquals(expectedResponse, result);
        verify(spyClient).get("/bookings/" + bookingId, null, userId);
    }

    @Test
    void getUserBookings_ShouldCallGetWithCorrectParameters() {
        Long userId = 1L;
        String state = "ALL";
        int from = 0;
        int size = 10;
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("bookings");

        BookingClient spyClient = spy(new BookingClient(restTemplate));
        doReturn(expectedResponse).when(spyClient).get(anyString(), any(), eq(userId));

        ResponseEntity<Object> result = spyClient.getUserBookings(userId, state, from, size);

        assertEquals(expectedResponse, result);
        verify(spyClient).get(eq("/bookings?state=" + state + "&from=" + from + "&size=" + size), isNull(), eq(userId));
    }

    @Test
    void getOwnerBookings_ShouldCallGetWithCorrectParameters() {
        Long userId = 1L;
        String state = "WAITING";
        int from = 0;
        int size = 10;
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("owner bookings");

        BookingClient spyClient = spy(new BookingClient(restTemplate));
        doReturn(expectedResponse).when(spyClient).get(anyString(), any(), eq(userId));

        ResponseEntity<Object> result = spyClient.getOwnerBookings(userId, state, from, size);

        assertEquals(expectedResponse, result);
        verify(spyClient).get(eq("/bookings/owner?state=" + state + "&from=" + from + "&size=" + size), isNull(), eq(userId));
    }

    @Test
    void constructor_ShouldCreateClientWithCorrectServerUrl() {
        String serverUrl = "http://test-server:8080";
        BookingClient client = new BookingClient(restTemplate);

        assertNotNull(client);
    }
}