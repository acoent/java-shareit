package ru.practicum.shareit.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class BookingClientNullApprovedTest {

    private RestTemplate rest;
    private BookingClient bookingClient;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        rest = mock(RestTemplate.class);
        bookingClient = new BookingClient(rest, "http://server");
    }

    @Test
    void updateBooking_withNullApproved_sendsNullLiteralInQuery() {
        when(rest.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(ResponseEntity.ok().build());

        bookingClient.updateBooking(11L, 22L, null);

        verify(rest).exchange(eq("http://server/bookings/22?approved=null"), eq(HttpMethod.PATCH), any(HttpEntity.class), eq(Object.class));
    }

    @Test
    void getUserBookings_customFromSize_buildsQueryCorrectly() {
        when(rest.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(ResponseEntity.ok().build());

        bookingClient.getUserBookings(3L, "REJECTED", 5, 50);

        verify(rest).exchange(eq("http://server/bookings?state=REJECTED&from=5&size=50"), eq(HttpMethod.GET), any(HttpEntity.class), eq(Object.class));
    }
}
