package ru.practicum.shareit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.client.BookingClient;
import ru.practicum.shareit.dto.BookingDto;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingClient bookingClient;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    void createBooking_ShouldReturnCreatedBooking() throws Exception {
        Long userId = 1L;
        BookingDto bookingDto = BookingDto.builder()
                .itemId(1L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();

        BookingDto createdBooking = BookingDto.builder()
                .id(1L)
                .itemId(1L)
                .start(bookingDto.getStart())
                .end(bookingDto.getEnd())
                .build();

        when(bookingClient.createBooking(eq(userId), any(BookingDto.class)))
                .thenReturn(ResponseEntity.ok(createdBooking));

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.itemId").value(1L));
    }

    @Test
    void createBooking_WithInvalidData_ShouldReturnBadRequest() throws Exception {
        Long userId = 1L;
        BookingDto invalidBooking = BookingDto.builder()
                .itemId(null)
                .start(null)
                .end(null)
                .build();

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidBooking)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createBooking_WithPastDates_ShouldReturnBadRequest() throws Exception {
        Long userId = 1L;
        BookingDto invalidBooking = BookingDto.builder()
                .itemId(1L)
                .start(LocalDateTime.now().minusDays(1))
                .end(LocalDateTime.now().minusDays(2))
                .build();

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidBooking)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateBooking_ShouldReturnUpdatedBooking() throws Exception {
        Long userId = 1L;
        Long bookingId = 1L;
        Boolean approved = true;

        when(bookingClient.updateBooking(userId, bookingId, approved))
                .thenReturn(ResponseEntity.ok("Updated booking"));

        mockMvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", userId)
                        .param("approved", approved.toString()))
                .andExpect(status().isOk());
    }

    @Test
    void getBooking_ShouldReturnBooking() throws Exception {
        Long userId = 1L;
        Long bookingId = 1L;

        BookingDto booking = BookingDto.builder()
                .id(bookingId)
                .itemId(1L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();

        when(bookingClient.getBooking(userId, bookingId))
                .thenReturn(ResponseEntity.ok(booking));

        mockMvc.perform(get("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingId))
                .andExpect(jsonPath("$.itemId").value(1L));
    }

    @Test
    void getUserBookings_ShouldReturnUserBookings() throws Exception {
        Long userId = 1L;
        String state = "ALL";

        when(bookingClient.getUserBookings(userId, state))
                .thenReturn(ResponseEntity.ok("[]"));

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .param("state", state)
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk());
    }

    @Test
    void getUserBookings_WithDefaultParameters_ShouldReturnUserBookings() throws Exception {
        Long userId = 1L;

        when(bookingClient.getUserBookings(userId, "ALL"))
                .thenReturn(ResponseEntity.ok("[]"));

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk());
    }

//    @Test
//    void getUserBookings_WithInvalidPaginationParameters_ShouldReturnBadRequest() throws Exception {
//        Long userId = 1L;
//
//        mockMvc.perform(get("/bookings")
//                        .header("X-Sharer-User-Id", userId)
//                        .param("from", "-1")
//                        .param("size", "0"))
//                .andExpect(status().isBadRequest());
//    }

    @Test
    void getOwnerBookings_ShouldReturnOwnerBookings() throws Exception {
        Long userId = 1L;
        String state = "WAITING";

        when(bookingClient.getOwnerBookings(userId, state))
                .thenReturn(ResponseEntity.ok("[]"));

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", userId)
                        .param("state", state)
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk());
    }

    @Test
    void getOwnerBookings_WithDefaultParameters_ShouldReturnOwnerBookings() throws Exception {
        Long userId = 1L;

        when(bookingClient.getOwnerBookings(userId, "ALL"))
                .thenReturn(ResponseEntity.ok("[]"));

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk());
    }
}