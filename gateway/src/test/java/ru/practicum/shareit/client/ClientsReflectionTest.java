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
import ru.practicum.shareit.dto.BookingDto;
import ru.practicum.shareit.dto.ItemDto;
import ru.practicum.shareit.dto.ItemRequestDto;
import ru.practicum.shareit.dto.UserDto;

import java.lang.reflect.Field;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ClientsReflectionTest {

    @Captor
    private ArgumentCaptor<HttpEntity> httpEntityCaptor;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
        httpEntityCaptor = ArgumentCaptor.forClass(HttpEntity.class);
    }

    private void injectMockRest(Object clientInstance, RestTemplate restMock) throws Exception {
        Field f = BaseClient.class.getDeclaredField("rest");
        f.setAccessible(true);
        f.set(clientInstance, restMock);
    }

    @Test
    void bookingClient_methods_shouldSetHeadersAndCallExchange() throws Exception {
        RestTemplate rest = mock(RestTemplate.class);
        when(rest.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(ResponseEntity.ok().build());
        BookingClient bookingClient = new BookingClient(rest, "http://server");
        BookingDto dto = BookingDto.builder().itemId(1L).start(LocalDateTime.now().plusDays(1)).end(LocalDateTime.now().plusDays(2)).build();
        bookingClient.createBooking(2L, dto);
        verify(rest).exchange(eq("http://server/bookings"), eq(HttpMethod.POST), httpEntityCaptor.capture(), eq(Object.class));
        assertThat(httpEntityCaptor.getValue().getHeaders().getFirst("X-Sharer-User-Id")).isEqualTo("2");

        reset(rest);

        when(rest.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(ResponseEntity.ok().build());
        bookingClient.updateBooking(3L, 10L, true);
        verify(rest).exchange(eq("http://server/bookings/10?approved=true"), eq(HttpMethod.PATCH), httpEntityCaptor.capture(), eq(Object.class));
        assertThat(httpEntityCaptor.getValue().getHeaders().getFirst("X-Sharer-User-Id")).isEqualTo("3");

        reset(rest);

        when(rest.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), eq(Object.class), anyMap()))
                .thenReturn(ResponseEntity.ok().build());
        bookingClient.getUserBookings(4L, "ALL", 0, 10);
        verify(rest).exchange(eq("http://server/bookings?state=ALL&from=0&size=10"), eq(HttpMethod.GET), httpEntityCaptor.capture(), eq(Object.class));
    }

    @Test
    void itemClient_methods_shouldSetHeadersAndCallExchange() throws Exception {
        RestTemplate rest = mock(RestTemplate.class);
        when(rest.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(ResponseEntity.ok().build());
        ItemClient itemClient = new ItemClient(rest, "http://server");
        ItemDto dto = ItemDto.builder().name("name").description("desc").available(true).build();
        itemClient.createItem(5L, dto);
        verify(rest).exchange(eq("http://server/items"), eq(HttpMethod.POST), httpEntityCaptor.capture(), eq(Object.class));
        assertThat(httpEntityCaptor.getValue().getHeaders().getFirst("X-Sharer-User-Id")).isEqualTo("5");

        reset(rest);

        when(rest.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), eq(Object.class), anyMap()))
                .thenReturn(ResponseEntity.ok().build());
        itemClient.searchItems(6L, "text", 0, 10);
        verify(rest).exchange(eq("http://server/items/search?text=text&from=0&size=10"), eq(HttpMethod.GET), httpEntityCaptor.capture(), eq(Object.class));
    }

    @Test
    void itemRequestClient_methods_shouldSetHeadersAndCallExchange() throws Exception {
        RestTemplate rest = mock(RestTemplate.class);
        when(rest.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(ResponseEntity.ok().build());
        ItemRequestClient reqClient = new ItemRequestClient(rest, "http://server");
        ItemRequestDto dto = ItemRequestDto.builder().description("need it").build();
        reqClient.createRequest(7L, dto);
        verify(rest).exchange(eq("http://server/requests"), eq(HttpMethod.POST), httpEntityCaptor.capture(), eq(Object.class));
        assertThat(httpEntityCaptor.getValue().getHeaders().getFirst("X-Sharer-User-Id")).isEqualTo("7");

        reset(rest);

        when(rest.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), eq(Object.class), anyMap()))
                .thenReturn(ResponseEntity.ok().build());
        reqClient.getAllRequests(8L, 0, 10);
        verify(rest).exchange(eq("http://server/requests/all?from=0&size=10"), eq(HttpMethod.GET), httpEntityCaptor.capture(), eq(Object.class));
    }

    @Test
    void userClient_methods_shouldSetHeadersCorrectly() throws Exception {
        RestTemplate rest = mock(RestTemplate.class);
        when(rest.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(ResponseEntity.ok().build());
        UserClient userClient = new UserClient(rest, "http://server");
        UserDto dto = UserDto.builder().name("u").email("u@example.com").build();
        userClient.createUser(dto);
        verify(rest).exchange(eq("http://server/users"), eq(HttpMethod.POST), httpEntityCaptor.capture(), eq(Object.class));
        assertThat(httpEntityCaptor.getValue().getHeaders().containsKey("X-Sharer-User-Id")).isFalse();

        reset(rest);

        when(rest.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(ResponseEntity.ok().build());
        userClient.updateUser(9L, dto);
        verify(rest).exchange(eq("http://server/users/9"), eq(HttpMethod.PATCH), httpEntityCaptor.capture(), eq(Object.class));
        assertThat(httpEntityCaptor.getValue().getHeaders().getFirst("X-Sharer-User-Id")).isEqualTo("9");

        reset(rest);

        when(rest.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), eq(Object.class), anyMap()))
                .thenReturn(ResponseEntity.ok().build());
        userClient.getAllUsers();
        verify(rest).exchange(eq("http://server/users"), eq(HttpMethod.GET), httpEntityCaptor.capture(), eq(Object.class));
    }
}
