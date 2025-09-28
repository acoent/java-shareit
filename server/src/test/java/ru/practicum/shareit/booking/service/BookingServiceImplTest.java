package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @Mock
    private BookingRepository bookingRepo;

    @Mock
    private ItemRepository itemRepo;

    @Mock
    private UserRepository userRepo;

    @Mock
    private BookingMapper bookingMapper;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private User user;
    private User owner;
    private Item item;
    private BookingDto bookingDto;
    private Booking booking;
    private BookingResponseDto responseDto;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .name("John Doe")
                .email("john@example.com")
                .build();

        owner = User.builder()
                .id(2L)
                .name("Jane Owner")
                .email("jane@example.com")
                .build();

        item = Item.builder()
                .id(1L)
                .name("Test Item")
                .description("Test Description")
                .available(true)
                .owner(owner)
                .build();

        bookingDto = BookingDto.builder()
                .itemId(1L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();

        booking = Booking.builder()
                .id(1L)
                .start(bookingDto.getStart())
                .end(bookingDto.getEnd())
                .item(item)
                .booker(user)
                .status(BookingStatus.WAITING)
                .build();

        responseDto = BookingResponseDto.builder()
                .id(1L)
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(BookingStatus.WAITING)
                .build();
    }

    @Test
    void create_Success() {
        when(userRepo.findById(1L)).thenReturn(Optional.of(user));
        when(itemRepo.findById(1L)).thenReturn(Optional.of(item));
        when(bookingMapper.toModel(bookingDto)).thenReturn(booking);
        when(bookingRepo.save(any(Booking.class))).thenReturn(booking);
        when(bookingMapper.toResponseDto(booking)).thenReturn(responseDto);

        BookingResponseDto result = bookingService.create(1L, bookingDto);

        assertNotNull(result);
        assertEquals(responseDto.getId(), result.getId());
        verify(bookingRepo).save(any(Booking.class));
    }

    @Test
    void create_NullDto_ThrowsBadRequestException() {
        assertThrows(BadRequestException.class, () -> bookingService.create(1L, null));
    }

    @Test
    void create_UserNotFound_ThrowsNotFoundException() {
        when(userRepo.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.create(1L, bookingDto));
    }

    @Test
    void create_ItemNotFound_ThrowsNotFoundException() {
        when(userRepo.findById(1L)).thenReturn(Optional.of(user));
        when(itemRepo.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.create(1L, bookingDto));
    }

    @Test
    void create_OwnerBookingOwnItem_ThrowsNotFoundException() {
        when(userRepo.findById(2L)).thenReturn(Optional.of(owner));
        when(itemRepo.findById(1L)).thenReturn(Optional.of(item));

        assertThrows(NotFoundException.class, () -> bookingService.create(2L, bookingDto));
    }

    @Test
    void create_ItemNotAvailable_ThrowsBadRequestException() {
        item.setAvailable(false);
        when(userRepo.findById(1L)).thenReturn(Optional.of(user));
        when(itemRepo.findById(1L)).thenReturn(Optional.of(item));

        assertThrows(BadRequestException.class, () -> bookingService.create(1L, bookingDto));
    }

    @Test
    void approve_Success() {
        when(bookingRepo.findById(1L)).thenReturn(Optional.of(booking));
        when(bookingRepo.save(any(Booking.class))).thenReturn(booking);
        when(bookingMapper.toResponseDto(booking)).thenReturn(responseDto);

        BookingResponseDto result = bookingService.approve(2L, 1L, true);

        assertNotNull(result);
        verify(bookingRepo).save(any(Booking.class));
    }

    @Test
    void approve_BookingNotFound_ThrowsNotFoundException() {
        when(bookingRepo.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.approve(2L, 1L, true));
    }

    @Test
    void approve_NotOwner_ThrowsForbiddenException() {
        when(bookingRepo.findById(1L)).thenReturn(Optional.of(booking));

        assertThrows(ForbiddenException.class, () -> bookingService.approve(3L, 1L, true));
    }

    @Test
    void approve_NotWaitingStatus_ThrowsBadRequestException() {
        booking.setStatus(BookingStatus.APPROVED);
        when(bookingRepo.findById(1L)).thenReturn(Optional.of(booking));

        assertThrows(BadRequestException.class, () -> bookingService.approve(2L, 1L, true));
    }

    @Test
    void getById_Success() {
        when(bookingRepo.findById(1L)).thenReturn(Optional.of(booking));
        when(bookingMapper.toResponseDto(booking)).thenReturn(responseDto);

        BookingResponseDto result = bookingService.getById(1L, 1L);

        assertNotNull(result);
        assertEquals(responseDto.getId(), result.getId());
    }

    @Test
    void getById_NotAccessible_ThrowsNotFoundException() {
        when(bookingRepo.findById(1L)).thenReturn(Optional.of(booking));

        assertThrows(NotFoundException.class, () -> bookingService.getById(3L, 1L));
    }

    @Test
    void getByBooker_Success() {
        Page<Booking> page = new PageImpl<>(List.of(booking));
        when(userRepo.existsById(1L)).thenReturn(true);
        when(bookingRepo.findByBooker_IdOrderByStartDesc(eq(1L), any(PageRequest.class))).thenReturn(page);
        when(bookingMapper.toResponseDto(booking)).thenReturn(responseDto);

        List<BookingResponseDto> result = bookingService.getByBooker(1L, "ALL", 0, 10);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    void getByBooker_UserNotFound_ThrowsNotFoundException() {
        when(userRepo.existsById(1L)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> bookingService.getByBooker(1L, "ALL", 0, 10));
    }

    @Test
    void getByBooker_InvalidSize_ThrowsBadRequestException() {
        when(userRepo.existsById(1L)).thenReturn(true);

        assertThrows(BadRequestException.class, () -> bookingService.getByBooker(1L, "ALL", 0, 0));
    }

    @Test
    void getByBooker_CurrentState() {
        Page<Booking> page = new PageImpl<>(List.of(booking));
        when(userRepo.existsById(1L)).thenReturn(true);
        when(bookingRepo.findByBooker_IdAndStartBeforeAndEndAfterOrderByStartDesc(
                eq(1L), any(LocalDateTime.class), any(LocalDateTime.class), any(PageRequest.class))).thenReturn(page);
        when(bookingMapper.toResponseDto(booking)).thenReturn(responseDto);

        List<BookingResponseDto> result = bookingService.getByBooker(1L, "CURRENT", 0, 10);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void getByBooker_UnknownState_ThrowsBadRequestException() {
        when(userRepo.existsById(1L)).thenReturn(true);

        assertThrows(BadRequestException.class, () -> bookingService.getByBooker(1L, "UNKNOWN", 0, 10));
    }

    @Test
    void getByOwner_Success() {
        Page<Booking> page = new PageImpl<>(List.of(booking));
        when(userRepo.existsById(2L)).thenReturn(true);
        when(bookingRepo.findByItem_Owner_IdOrderByStartDesc(eq(2L), any(PageRequest.class))).thenReturn(page);
        when(bookingMapper.toResponseDto(booking)).thenReturn(responseDto);

        List<BookingResponseDto> result = bookingService.getByOwner(2L, "ALL", 0, 10);

        assertNotNull(result);
        assertEquals(1, result.size());
    }
}
