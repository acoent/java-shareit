package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    @Mock
    private ItemRepository itemRepo;

    @Mock
    private UserRepository userRepo;

    @Mock
    private CommentRepository commentRepo;

    @Mock
    private BookingRepository bookingRepo;

    @Mock
    private ItemMapper itemMapper;

    @Mock
    private CommentMapper commentMapper;

    @InjectMocks
    private ItemServiceImpl itemService;

    private User user;
    private Item item;
    private ItemDto itemDto;
    private Comment comment;
    private CommentDto commentDto;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .name("John Doe")
                .email("john@example.com")
                .build();

        item = Item.builder()
                .id(1L)
                .name("Test Item")
                .description("Test Description")
                .available(true)
                .owner(user)
                .build();

        itemDto = ItemDto.builder()
                .id(1L)
                .name("Test Item")
                .description("Test Description")
                .available(true)
                .ownerId(1L)
                .build();

        comment = Comment.builder()
                .id(1L)
                .text("Great item!")
                .author(user)
                .item(item)
                .created(LocalDateTime.now())
                .build();

        commentDto = CommentDto.builder()
                .id(1L)
                .text("Great item!")
                .authorId(1L)
                .authorName("John Doe")
                .itemId(1L)
                .created(LocalDateTime.now())
                .build();
    }

    @Test
    void create_Success() {
        when(userRepo.findById(1L)).thenReturn(Optional.of(user));
        when(itemMapper.toModel(itemDto)).thenReturn(item);
        when(itemRepo.save(any(Item.class))).thenReturn(item);
        when(itemMapper.toDto(item)).thenReturn(itemDto);

        ItemDto result = itemService.create(1L, itemDto);

        assertNotNull(result);
        assertEquals(itemDto.getName(), result.getName());
        verify(itemRepo).save(any(Item.class));
    }

    @Test
    void create_UserNotFound_ThrowsNotFoundException() {
        when(userRepo.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.create(1L, itemDto));
    }

    @Test
    void update_Success() {
        when(itemRepo.findById(1L)).thenReturn(Optional.of(item));
        when(itemRepo.save(any(Item.class))).thenReturn(item);
        when(itemMapper.toDto(item)).thenReturn(itemDto);

        ItemDto updateDto = ItemDto.builder().name("Updated Name").build();

        ItemDto result = itemService.update(1L, 1L, updateDto);

        assertNotNull(result);
        verify(itemRepo).save(any(Item.class));
    }

    @Test
    void update_ItemNotFound_ThrowsNotFoundException() {
        when(itemRepo.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () ->
                itemService.update(1L, 1L, itemDto));
    }

    @Test
    void update_NotOwner_ThrowsNotFoundException() {
        when(itemRepo.findById(1L)).thenReturn(Optional.of(item));

        assertThrows(NotFoundException.class, () ->
                itemService.update(2L, 1L, itemDto));
    }

    @Test
    void getById_Success() {
        when(itemRepo.findById(1L)).thenReturn(Optional.of(item));
        when(commentRepo.findByItem_IdOrderByCreatedDesc(1L)).thenReturn(Collections.emptyList());
        when(itemMapper.toResponseDto(eq(item), any(), any(), any())).thenReturn(new ItemResponseDto());

        ItemResponseDto result = itemService.getById(1L, 1L);

        assertNotNull(result);
        verify(itemRepo).findById(1L);
    }

    @Test
    void getById_ItemNotFound_ThrowsNotFoundException() {
        when(itemRepo.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.getById(1L, 1L));
    }

    @Test
    void getByOwner_Success() {
        Page<Item> itemsPage = new PageImpl<>(List.of(item));
        when(userRepo.findById(1L)).thenReturn(Optional.of(user));
        when(itemRepo.findAllByOwner_Id(eq(1L), any(PageRequest.class))).thenReturn(itemsPage);
        when(commentRepo.findByItem_IdOrderByCreatedDesc(1L)).thenReturn(Collections.emptyList());
        when(itemMapper.toResponseDto(eq(item), any(), any(), any())).thenReturn(new ItemResponseDto());

        List<ItemResponseDto> result = itemService.getByOwner(1L, 0, 10);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void getByOwner_InvalidSize_ThrowsBadRequestException() {
        when(userRepo.findById(1L)).thenReturn(Optional.of(user));

        assertThrows(BadRequestException.class, () ->
                itemService.getByOwner(1L, 0, 0));
    }

    @Test
    void search_Success() {
        when(itemRepo.search(eq("test"), any(PageRequest.class))).thenReturn(List.of(item));
        when(itemMapper.toDto(item)).thenReturn(itemDto);

        List<ItemDto> result = itemService.search("test", 0, 10);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void search_EmptyText_ReturnsEmptyList() {
        List<ItemDto> result = itemService.search("", 0, 10);

        assertTrue(result.isEmpty());
    }

    @Test
    void addComment_Success() {
        Booking booking = Booking.builder()
                .id(1L)
                .booker(user)
                .item(item)
                .status(BookingStatus.APPROVED)
                .end(LocalDateTime.now().minusDays(1))
                .build();

        when(userRepo.findById(1L)).thenReturn(Optional.of(user));
        when(itemRepo.findById(1L)).thenReturn(Optional.of(item));
        when(bookingRepo.findByBooker_IdAndItem_IdAndStatusAndEndBefore(
                eq(1L), eq(1L), eq(BookingStatus.APPROVED), any(LocalDateTime.class)))
                .thenReturn(List.of(booking));
        when(commentMapper.toModel(commentDto)).thenReturn(comment);
        when(commentRepo.save(any(Comment.class))).thenReturn(comment);
        when(commentMapper.toDto(comment)).thenReturn(commentDto);

        CommentDto result = itemService.addComment(1L, 1L, commentDto);

        assertNotNull(result);
        assertEquals(commentDto.getText(), result.getText());
        verify(commentRepo).save(any(Comment.class));
    }

    @Test
    void addComment_NoBooking_ThrowsBadRequestException() {
        when(userRepo.findById(1L)).thenReturn(Optional.of(user));
        when(itemRepo.findById(1L)).thenReturn(Optional.of(item));
        when(bookingRepo.findByBooker_IdAndItem_IdAndStatusAndEndBefore(
                eq(1L), eq(1L), eq(BookingStatus.APPROVED), any(LocalDateTime.class)))
                .thenReturn(Collections.emptyList());

        assertThrows(BadRequestException.class, () ->
                itemService.addComment(1L, 1L, commentDto));
    }

    @Test
    void getByRequestId_Success() {
        when(itemRepo.findByRequestId(1L)).thenReturn(List.of(item));
        when(itemMapper.toDto(item)).thenReturn(itemDto);

        List<ItemDto> result = itemService.getByRequestId(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
    }
}