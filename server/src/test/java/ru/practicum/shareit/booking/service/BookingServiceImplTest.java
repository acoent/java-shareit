package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

    private User booker;
    private User owner;
    private Item item;
    private Booking booking;
    private BookingDto bookingDto;
    private BookingResponseDto responseDto;

    @BeforeEach
    void setUp() {
        booker = User.builder().id(1L).name("Booker").email("b@e").build();
        owner = User.builder().id(2L).name("Owner").email("o@e").build();
        item = Item.builder().id(10L).name("Drill").description("Drill").available(true).owner(owner).build();

        bookingDto = BookingDto.builder()
                .itemId(item.getId())
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();

        booking = Booking.builder()
                .id(100L)
                .start(bookingDto.getStart())
                .end(bookingDto.getEnd())
                .item(item)
                .booker(booker)
                .status(BookingStatus.WAITING)
                .build();

        responseDto = BookingResponseDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(booking.getStatus())
                .build();
    }

    @Test
    void create_Success() {
        when(userRepo.findById(booker.getId())).thenReturn(Optional.of(booker));
        when(itemRepo.findById(item.getId())).thenReturn(Optional.of(item));
        when(bookingMapper.toModel(bookingDto)).thenReturn(booking);
        when(bookingRepo.save(any(Booking.class))).thenReturn(booking);
        when(bookingMapper.toResponseDto(booking)).thenReturn(responseDto);

        BookingResponseDto result = bookingService.create(booker.getId(), bookingDto);

        assertNotNull(result);
        assertEquals(responseDto.getId(), result.getId());
        verify(bookingRepo).save(any(Booking.class));
    }

    @Test
    void create_ItemNotAvailable_ThrowsBadRequest() {
        item.setAvailable(false);
        when(userRepo.findById(booker.getId())).thenReturn(Optional.of(booker));
        when(itemRepo.findById(item.getId())).thenReturn(Optional.of(item));

        assertThrows(BadRequestException.class, () -> bookingService.create(booker.getId(), bookingDto));
    }

    @Test
    void create_UserNotFound_ThrowsNotFound() {
        when(userRepo.findById(booker.getId())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> bookingService.create(booker.getId(), bookingDto));
    }

    @Test
    void create_OwnerBookingOwnItem_ThrowsNotFound() {
        when(userRepo.findById(owner.getId())).thenReturn(Optional.of(owner));
        when(itemRepo.findById(item.getId())).thenReturn(Optional.of(item));

        assertThrows(NotFoundException.class, () -> bookingService.create(owner.getId(), bookingDto));
    }

    @Test
    void approve_Success_ApproveTrue() {
        when(bookingRepo.findById(booking.getId())).thenReturn(Optional.of(booking));
        when(bookingRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(bookingMapper.toResponseDto(any())).thenReturn(responseDto);

        BookingResponseDto res = bookingService.approve(owner.getId(), booking.getId(), true);
        assertNotNull(res);

        ArgumentCaptor<Booking> captor = ArgumentCaptor.forClass(Booking.class);
        verify(bookingRepo).save(captor.capture());
        assertEquals(BookingStatus.APPROVED, captor.getValue().getStatus());
    }

    @Test
    void approve_Success_ApproveFalse() {
        when(bookingRepo.findById(booking.getId())).thenReturn(Optional.of(booking));
        when(bookingRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(bookingMapper.toResponseDto(any())).thenReturn(responseDto);

        BookingResponseDto res = bookingService.approve(owner.getId(), booking.getId(), false);
        assertNotNull(res);

        ArgumentCaptor<Booking> captor = ArgumentCaptor.forClass(Booking.class);
        verify(bookingRepo).save(captor.capture());
        assertEquals(BookingStatus.REJECTED, captor.getValue().getStatus());
    }

    @Test
    void approve_NotOwner_ThrowsForbidden() {
        when(bookingRepo.findById(booking.getId())).thenReturn(Optional.of(booking));
        assertThrows(ForbiddenException.class, () -> bookingService.approve(999L, booking.getId(), true));
    }

    @Test
    void approve_AlreadyApproved_ThrowsBadRequest() {
        booking.setStatus(BookingStatus.APPROVED);
        when(bookingRepo.findById(booking.getId())).thenReturn(Optional.of(booking));
        assertThrows(BadRequestException.class, () -> bookingService.approve(owner.getId(), booking.getId(), true));
    }

    @Test
    void getByBooker_AllState() {
        Page<Booking> page = new PageImpl<>(List.of(booking));
        when(userRepo.existsById(booker.getId())).thenReturn(true);
        when(bookingRepo.findByBooker_IdOrderByStartDesc(eq(booker.getId()), any(Pageable.class))).thenReturn(page);
        when(bookingMapper.toResponseDto(booking)).thenReturn(responseDto);

        List<BookingResponseDto> result = bookingService.getByBooker(booker.getId(), "ALL", 0, 10);

        assertEquals(1, result.size());
    }

    @Test
    void getByBooker_WaitingRejectedCurrentFuturePast() {
        when(userRepo.existsById(booker.getId())).thenReturn(true);
        Page<Booking> p = new PageImpl<>(List.of(booking));
        when(bookingMapper.toResponseDto(any())).thenReturn(responseDto);

        when(bookingRepo.findByBooker_IdAndStatusOrderByStartDesc(eq(booker.getId()), eq(BookingStatus.WAITING), any())).thenReturn(p);
        when(bookingRepo.findByBooker_IdAndStatusOrderByStartDesc(eq(booker.getId()), eq(BookingStatus.REJECTED), any())).thenReturn(p);
        when(bookingRepo.findByBooker_IdAndStartBeforeAndEndAfterOrderByStartDesc(eq(booker.getId()), any(), any(), any())).thenReturn(p);
        when(bookingRepo.findByBooker_IdAndStartAfterOrderByStartDesc(eq(booker.getId()), any(), any())).thenReturn(p);
        when(bookingRepo.findByBooker_IdAndEndBeforeOrderByStartDesc(eq(booker.getId()), any(), any())).thenReturn(p);

        assertEquals(1, bookingService.getByBooker(booker.getId(), "WAITING", 0, 10).size());
        assertEquals(1, bookingService.getByBooker(booker.getId(), "REJECTED", 0, 10).size());
        assertEquals(1, bookingService.getByBooker(booker.getId(), "CURRENT", 0, 10).size());
        assertEquals(1, bookingService.getByBooker(booker.getId(), "FUTURE", 0, 10).size());
        assertEquals(1, bookingService.getByBooker(booker.getId(), "PAST", 0, 10).size());
    }

    @Test
    void getByBooker_UserNotFound_ThrowsNotFound() {
        when(userRepo.existsById(booker.getId())).thenReturn(false);
        assertThrows(NotFoundException.class, () -> bookingService.getByBooker(booker.getId(), "ALL", 0, 10));
    }

    @Test
    void getByBooker_InvalidSize_ThrowsBadRequest() {
        when(userRepo.existsById(booker.getId())).thenReturn(true);
        assertThrows(BadRequestException.class, () -> bookingService.getByBooker(booker.getId(), "ALL", 0, 0));
    }

    @Test
    void getByOwner_AllStates() {
        when(userRepo.existsById(owner.getId())).thenReturn(true);
        Page<Booking> page = new PageImpl<>(List.of(booking));
        when(bookingMapper.toResponseDto(any())).thenReturn(responseDto);

        when(bookingRepo.findByItem_Owner_IdOrderByStartDesc(eq(owner.getId()), any(Pageable.class))).thenReturn(page);
        when(bookingRepo.findByItem_Owner_IdAndStatusOrderByStartDesc(eq(owner.getId()), eq(BookingStatus.WAITING), any())).thenReturn(page);
        when(bookingRepo.findByItem_Owner_IdAndStatusOrderByStartDesc(eq(owner.getId()), eq(BookingStatus.REJECTED), any())).thenReturn(page);
        when(bookingRepo.findByItem_Owner_IdAndStartAfterOrderByStartDesc(eq(owner.getId()), any(), any())).thenReturn(page);

        assertEquals(1, bookingService.getByOwner(owner.getId(), "ALL", 0, 10).size());
        assertEquals(1, bookingService.getByOwner(owner.getId(), "WAITING", 0, 10).size());
        assertEquals(1, bookingService.getByOwner(owner.getId(), "REJECTED", 0, 10).size());
        assertEquals(1, bookingService.getByOwner(owner.getId(), "FUTURE", 0, 10).size());
    }

    @Test
    void getByOwner_UserNotFound_ThrowsNotFound() {
        when(userRepo.existsById(owner.getId())).thenReturn(false);
        assertThrows(NotFoundException.class, () -> bookingService.getByOwner(owner.getId(), "ALL", 0, 10));
    }

    @Test
    void getByOwner_InvalidSize_ThrowsBadRequest() {
        when(userRepo.existsById(owner.getId())).thenReturn(true);

        assertThrows(BadRequestException.class, () -> bookingService.getByOwner(owner.getId(), "ALL", 0, 0));
    }

    @Test
    void getByOwner_StateCurrent_ShouldReturnCurrentBookings() {
        when(userRepo.existsById(owner.getId())).thenReturn(true);
        Page<Booking> page = new PageImpl<>(List.of(booking));
        when(bookingRepo.findByItem_Owner_IdAndStartBeforeAndEndAfterOrderByStartDesc(anyLong(), any(), any(), any(Pageable.class)))
                .thenReturn(page);
        when(bookingMapper.toResponseDto(any(Booking.class))).thenReturn(responseDto);

        List<BookingResponseDto> result = bookingService.getByOwner(owner.getId(), "CURRENT", 0, 10);

        assertNotNull(result);
    }

    @Test
    void getByOwner_StatePast_ShouldReturnPastBookings() {
        when(userRepo.existsById(owner.getId())).thenReturn(true);
        Page<Booking> page = new PageImpl<>(List.of(booking));
        when(bookingRepo.findByItem_Owner_IdAndEndBeforeOrderByStartDesc(anyLong(), any(), any(Pageable.class)))
                .thenReturn(page);
        when(bookingMapper.toResponseDto(any(Booking.class))).thenReturn(responseDto);

        List<BookingResponseDto> result = bookingService.getByOwner(owner.getId(), "PAST", 0, 10);

        assertNotNull(result);
    }

    @Test
    void getByOwner_StateFuture_ShouldReturnFutureBookings() {
        when(userRepo.existsById(owner.getId())).thenReturn(true);
        Page<Booking> page = new PageImpl<>(List.of(booking));
        when(bookingRepo.findByItem_Owner_IdAndStartAfterOrderByStartDesc(anyLong(), any(), any(Pageable.class)))
                .thenReturn(page);
        when(bookingMapper.toResponseDto(any(Booking.class))).thenReturn(responseDto);

        List<BookingResponseDto> result = bookingService.getByOwner(owner.getId(), "FUTURE", 0, 10);

        assertNotNull(result);
    }

    @Test
    void getByOwner_StateWaiting_ShouldReturnWaitingBookings() {
        when(userRepo.existsById(owner.getId())).thenReturn(true);
        Page<Booking> page = new PageImpl<>(List.of(booking));
        when(bookingRepo.findByItem_Owner_IdAndStatusOrderByStartDesc(anyLong(), eq(BookingStatus.WAITING), any(Pageable.class)))
                .thenReturn(page);
        when(bookingMapper.toResponseDto(any(Booking.class))).thenReturn(responseDto);

        List<BookingResponseDto> result = bookingService.getByOwner(owner.getId(), "WAITING", 0, 10);

        assertNotNull(result);
    }

    @Test
    void getByOwner_StateRejected_ShouldReturnRejectedBookings() {
        when(userRepo.existsById(owner.getId())).thenReturn(true);
        Page<Booking> page = new PageImpl<>(List.of(booking));
        when(bookingRepo.findByItem_Owner_IdAndStatusOrderByStartDesc(anyLong(), eq(BookingStatus.REJECTED), any(Pageable.class)))
                .thenReturn(page);
        when(bookingMapper.toResponseDto(any(Booking.class))).thenReturn(responseDto);

        List<BookingResponseDto> result = bookingService.getByOwner(owner.getId(), "REJECTED", 0, 10);

        assertNotNull(result);
    }

    @Test
    void getByOwner_UnknownState_ShouldThrowBadRequestException() {
        when(userRepo.existsById(owner.getId())).thenReturn(true);
        assertThrows(BadRequestException.class, () -> bookingService.getByOwner(owner.getId(), "INVALID", 0, 10));
    }

    @Test
    void create_WithNullDto_ShouldThrowBadRequestException() {
        assertThrows(BadRequestException.class, () -> bookingService.create(1L, null));
    }


    @Test
    void create_ItemNotFound_ShouldThrowNotFoundException() {
        when(userRepo.findById(booker.getId())).thenReturn(Optional.of(booker));
        when(itemRepo.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.create(booker.getId(), bookingDto));
    }

    @Test
    void getByBooker_NegativeFrom_ShouldSetToZero() {
        when(userRepo.existsById(booker.getId())).thenReturn(true);
        Page<Booking> page = new PageImpl<>(List.of(booking));
        when(bookingRepo.findByBooker_IdOrderByStartDesc(anyLong(), any(PageRequest.class))).thenReturn(page);
        when(bookingMapper.toResponseDto(any(Booking.class))).thenReturn(responseDto);

        List<BookingResponseDto> result = bookingService.getByBooker(booker.getId(), "ALL", -5, 10);

        assertNotNull(result);
        verify(bookingRepo).findByBooker_IdOrderByStartDesc(eq(booker.getId()), any(PageRequest.class));
    }

    @Test
    void getByBooker_StateNullOrBlank_ShouldReturnAllBookings() {
        when(userRepo.existsById(booker.getId())).thenReturn(true);
        Page<Booking> page = new PageImpl<>(List.of(booking));
        when(bookingRepo.findByBooker_IdOrderByStartDesc(anyLong(), any(PageRequest.class))).thenReturn(page);
        when(bookingMapper.toResponseDto(any(Booking.class))).thenReturn(responseDto);

        List<BookingResponseDto> resultNull = bookingService.getByBooker(booker.getId(), null, 0, 10);
        List<BookingResponseDto> resultBlank = bookingService.getByBooker(booker.getId(), "", 0, 10);

        assertNotNull(resultNull);
        assertNotNull(resultBlank);
    }


    @Test
    void getById_BookingNotFound_ShouldThrowNotFoundException() {
        when(bookingRepo.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> bookingService.getById(1L, 1L));
    }

    @Test
    void getById_NotBookerOrOwner_ShouldThrowNotFoundException() {
        when(bookingRepo.findById(1L)).thenReturn(Optional.of(booking));
        assertThrows(NotFoundException.class, () -> bookingService.getById(999L, 1L));
    }

    @Test
    void getById_ByBooker_ShouldReturnBooking() {
        when(bookingRepo.findById(1L)).thenReturn(Optional.of(booking));
        when(bookingMapper.toResponseDto(booking)).thenReturn(responseDto);

        BookingResponseDto result = bookingService.getById(booker.getId(), 1L);

        assertNotNull(result);
    }

    @Test
    void getById_ByOwner_ShouldReturnBooking() {
        when(bookingRepo.findById(1L)).thenReturn(Optional.of(booking));
        when(bookingMapper.toResponseDto(booking)).thenReturn(responseDto);

        BookingResponseDto result = bookingService.getById(owner.getId(), 1L);

        assertNotNull(result);
    }

    @Test
    void approve_BookingNotFound_ShouldThrowNotFoundException() {
        when(bookingRepo.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> bookingService.approve(1L, 1L, true));
    }

    @Test
    void approve_ItemIsNull_ShouldThrowNotFoundException() {
        booking.setItem(null);
        when(bookingRepo.findById(1L)).thenReturn(Optional.of(booking));

        assertThrows(NotFoundException.class, () -> bookingService.approve(1L, 1L, true));
    }
}
