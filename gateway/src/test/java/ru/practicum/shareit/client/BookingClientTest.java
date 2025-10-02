package ru.practicum.shareit.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.common.HeaderConstants;
import ru.practicum.shareit.dto.BookingDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class BookingClientTest {

    private RestTemplate restTemplate;
    private BookingClient bookingClient;

    @Captor
    private ArgumentCaptor<HttpEntity> httpEntityCaptor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        restTemplate = mock(RestTemplate.class);
        bookingClient = new BookingClient(restTemplate);
        httpEntityCaptor = ArgumentCaptor.forClass(HttpEntity.class);
    }

    @Test
    void createBooking_shouldCallPostWithCorrectParameters() {
        Long userId = 1L;
        BookingDto bookingDto = BookingDto.builder()
                .itemId(1L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("created");

        BookingClient spyClient = spy(bookingClient);
        doReturn(expectedResponse).when(spyClient).post(eq("/bookings"), eq(bookingDto), eq(userId));

        ResponseEntity<Object> result = spyClient.createBooking(userId, bookingDto);

        assertEquals(expectedResponse, result);
        verify(spyClient).post("/bookings", bookingDto, userId);
    }

    @Test
    void createBooking_shouldSetCorrectHeaders() {
        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(ResponseEntity.ok().build());

        BookingDto dto = BookingDto.builder()
                .itemId(1L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();

        bookingClient.createBooking(2L, dto);

        verify(restTemplate).exchange(eq("/bookings"), eq(HttpMethod.POST), httpEntityCaptor.capture(), eq(Object.class));
        assertThat(httpEntityCaptor.getValue().getHeaders().getFirst(HeaderConstants.X_SHARER_USER_ID)).isEqualTo("2");
    }

    @Test
    void updateBooking_shouldCallPatchWithCorrectPath() {
        Long userId = 1L;
        Long bookingId = 2L;
        Boolean approved = true;
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("updated");

        BookingClient spyClient = spy(bookingClient);
        doReturn(expectedResponse).when(spyClient).patch(anyString(), isNull(), eq(userId));

        ResponseEntity<Object> result = spyClient.updateBooking(userId, bookingId, approved);

        assertEquals(expectedResponse, result);
        verify(spyClient).patch(contains("/bookings/2"), isNull(), eq(userId));
        verify(spyClient).patch(contains("approved=true"), isNull(), eq(userId));
    }

    @Test
    void updateBooking_shouldSetCorrectHeaders() {
        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(ResponseEntity.ok().build());

        bookingClient.updateBooking(3L, 10L, true);

        verify(restTemplate).exchange(contains("/bookings/10"), eq(HttpMethod.PATCH), httpEntityCaptor.capture(), eq(Object.class));
        assertThat(httpEntityCaptor.getValue().getHeaders().getFirst(HeaderConstants.X_SHARER_USER_ID)).isEqualTo("3");
    }

    @Test
    void getBooking_shouldCallGetWithCorrectPath() {
        Long userId = 1L;
        Long bookingId = 2L;
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("booking");

        BookingClient spyClient = spy(bookingClient);
        doReturn(expectedResponse).when(spyClient).get(anyString(), any(), eq(userId));

        ResponseEntity<Object> result = spyClient.getBooking(userId, bookingId);

        assertEquals(expectedResponse, result);
        verify(spyClient).get(contains("/bookings/2"), isNull(), eq(userId));
    }

    @Test
    void getUserBookings_shouldBuildQueryCorrectly() {
        Long userId = 1L;
        String state = "ALL";
        int from = 0;
        int size = 10;
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("bookings");

        BookingClient spyClient = spy(bookingClient);
        doReturn(expectedResponse).when(spyClient).get(anyString(), any(), eq(userId));

        ResponseEntity<Object> result = spyClient.getUserBookings(userId, state, from, size);

        assertEquals(expectedResponse, result);
        verify(spyClient).get(contains("state=ALL"), isNull(), eq(userId));
        verify(spyClient).get(contains("from=0"), isNull(), eq(userId));
        verify(spyClient).get(contains("size=10"), isNull(), eq(userId));
    }

    @Test
    void getUserBookings_withCustomFromSize_shouldBuildQueryCorrectly() {
        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(ResponseEntity.ok().build());

        bookingClient.getUserBookings(3L, "REJECTED", 5, 50);

        verify(restTemplate).exchange(contains("state=REJECTED"), eq(HttpMethod.GET), any(HttpEntity.class), eq(Object.class));
        verify(restTemplate).exchange(contains("from=5"), eq(HttpMethod.GET), any(HttpEntity.class), eq(Object.class));
        verify(restTemplate).exchange(contains("size=50"), eq(HttpMethod.GET), any(HttpEntity.class), eq(Object.class));
    }

    @Test
    void getOwnerBookings_shouldCallGetWithCorrectPath() {
        Long userId = 1L;
        String state = "WAITING";
        int from = 0;
        int size = 10;
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("owner bookings");

        BookingClient spyClient = spy(bookingClient);
        doReturn(expectedResponse).when(spyClient).get(anyString(), any(), eq(userId));

        ResponseEntity<Object> result = spyClient.getOwnerBookings(userId, state, from, size);

        assertEquals(expectedResponse, result);
        verify(spyClient).get(contains("/bookings/owner"), isNull(), eq(userId));
        verify(spyClient).get(contains("state=WAITING"), isNull(), eq(userId));
    }

    @Test
    void constructor_shouldCreateClientSuccessfully() {
        BookingClient client = new BookingClient(restTemplate);
        assertNotNull(client);
    }
}