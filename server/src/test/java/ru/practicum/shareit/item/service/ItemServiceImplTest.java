package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.BookingShortDto;
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
        user = User.builder().id(1L).name("John Doe").email("john@example.com").build();

        item = Item.builder().id(1L).name("Test Item").description("Test Description").available(true)
                .owner(user).build();

        itemDto = ItemDto.builder().id(1L).name("Test Item").description("Test Description").available(true)
                .ownerId(1L).build();

        comment = Comment.builder().id(1L).text("Great item!").author(user).item(item).created(LocalDateTime.now()).
                build();

        commentDto = CommentDto.builder().id(1L).text("Great item!").authorId(1L).authorName("John Doe").itemId(1L)
                .created(LocalDateTime.now()).build();
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
    void update_Success_partialUpdate_preservesFields() {
        Item original = Item.builder().id(2L).name("orig").description("origDesc").available(true).owner(user).build();

        when(itemRepo.findById(2L)).thenReturn(Optional.of(original));
        ArgumentCaptor<Item> captor = ArgumentCaptor.forClass(Item.class);
        when(itemRepo.save(captor.capture())).thenAnswer(inv -> inv.getArgument(0));
        when(itemMapper.toDto(any())).thenReturn(ItemDto.builder().id(2L).name("newName").description("origDesc")
                .available(true).ownerId(1L).build());

        ItemDto updateDto = ItemDto.builder().name("newName").build();
        ItemDto result = itemService.update(1L, 2L, updateDto);

        assertNotNull(result);
        Item saved = captor.getValue();
        assertEquals("newName", saved.getName());
        assertEquals("origDesc", saved.getDescription());
    }

    @Test
    void update_ItemNotFound_ThrowsNotFoundException() {
        when(itemRepo.findById(1L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> itemService.update(1L, 1L, itemDto));
    }

    @Test
    void update_NotOwner_ThrowsNotFoundException() {
        when(itemRepo.findById(1L)).thenReturn(Optional.of(item));
        assertThrows(NotFoundException.class, () -> itemService.update(2L, 1L, itemDto));
    }

    @Test
    void getById_Success_nonOwner_doesNotQueryBookings() {
        User otherUser = User.builder().id(999L).name("Other").email("o@e").build();
        when(itemRepo.findById(1L)).thenReturn(Optional.of(item));
        when(commentRepo.findByItem_IdOrderByCreatedDesc(1L)).thenReturn(Collections.emptyList());
        when(itemMapper.toResponseDto(eq(item), any(), any(), any())).thenReturn(new ItemResponseDto());

        ItemResponseDto resp = itemService.getById(otherUser.getId(), 1L);

        assertNotNull(resp);
        verify(itemRepo).findById(1L);
        verify(commentRepo).findByItem_IdOrderByCreatedDesc(1L);
        verifyNoInteractions(bookingRepo);
    }

    @Test
    void getById_ItemNotFound_ThrowsNotFoundException() {
        when(itemRepo.findById(1L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> itemService.getById(1L, 1L));
    }

    @Test
    void getById_owner_withLastAndNextBookings_populatesShortDtos() {
        User owner = user;
        Item itemLocal = Item.builder().id(10L).name("Saw").description("Electric saw").available(true).owner(owner)
                .build();

        when(itemRepo.findById(itemLocal.getId())).thenReturn(Optional.of(itemLocal));
        when(commentRepo.findByItem_IdOrderByCreatedDesc(itemLocal.getId())).thenReturn(Collections.emptyList());

        Booking last = Booking.builder().id(201L).start(LocalDateTime.now().minusDays(5)).end(LocalDateTime.now()
                .minusDays(4)).booker(User.builder().id(2L).build()).status(BookingStatus.APPROVED).build();

        Booking next = Booking.builder().id(202L).start(LocalDateTime.now().plusDays(2)).end(LocalDateTime.now()
                .plusDays(3)).booker(User.builder().id(2L).build()).status(BookingStatus.APPROVED).build();

        when(bookingRepo.findFirstByItem_IdAndStatusAndStartBeforeOrderByStartDesc(eq(itemLocal.getId()),
                eq(BookingStatus.APPROVED), any())).thenReturn(Optional.of(last));
        when(bookingRepo.findFirstByItem_IdAndStatusAndStartAfterOrderByStartAsc(eq(itemLocal.getId()),
                eq(BookingStatus.APPROVED), any())).thenReturn(Optional.of(next));

        doAnswer(inv -> {
            Item i = inv.getArgument(0);
            Object lastArg = inv.getArgument(1);
            Object nextArg = inv.getArgument(2);
            ItemResponseDto r = new ItemResponseDto();
            r.setId(i.getId());

            java.util.function.BiConsumer<Object, java.util.function.Consumer<ru.practicum.shareit.item.dto
                    .BookingShortDto>> setIfPresent = (arg, consumer) -> {
                if (arg == null) return;
                if (arg instanceof Booking b) {
                    consumer.accept(new ru.practicum.shareit.item.dto.BookingShortDto(b.getId(),
                            b.getBooker() != null ? b.getBooker().getId() : null));
                } else if (arg instanceof ru.practicum.shareit.item.dto.BookingShortDto) {
                    consumer.accept((ru.practicum.shareit.item.dto.BookingShortDto) arg);
                } else {
                    try {
                        java.lang.reflect.Method getId = arg.getClass().getMethod("getId");
                        Long id = (Long) getId.invoke(arg);
                        Long bookerId = null;
                        try {
                            java.lang.reflect.Method getBookerId = arg.getClass().getMethod("getBookerId");
                            Object bid = getBookerId.invoke(arg);
                            if (bid instanceof Long) bookerId = (Long) bid;
                        } catch (NoSuchMethodException ignored) {
                        }
                        consumer.accept(new ru.practicum.shareit.item.dto.BookingShortDto(id, bookerId));
                    } catch (Exception ignored) {
                    }
                }
            };

            setIfPresent.accept(lastArg, bs -> r.setLastBooking(bs));
            setIfPresent.accept(nextArg, bs -> r.setNextBooking(bs));
            r.setComments(Collections.emptyList());
            return r;
        }).when(itemMapper).toResponseDto(any(), any(), any(), any());


        ItemResponseDto resp = itemService.getById(owner.getId(), itemLocal.getId());
        assertNotNull(resp);
        assertNotNull(resp.getLastBooking());
        assertEquals(201L, resp.getLastBooking().getId());
        assertNotNull(resp.getNextBooking());
        assertEquals(202L, resp.getNextBooking().getId());

        verify(bookingRepo).findFirstByItem_IdAndStatusAndStartBeforeOrderByStartDesc(eq(itemLocal.getId()),
                eq(BookingStatus.APPROVED), any());
        verify(bookingRepo).findFirstByItem_IdAndStatusAndStartAfterOrderByStartAsc(eq(itemLocal.getId()),
                eq(BookingStatus.APPROVED), any());
    }

    @Test
    void getById_ownerRequester_getsLastNextNullBookerHandled() {
        Long requesterId = 3L;
        Long itemId = 20L;
        User owner = User.builder().id(requesterId).build();
        Item itemLocal = Item.builder().id(itemId).name("x").description("d").available(true).owner(owner).build();

        Booking last = Booking.builder().id(501L).start(LocalDateTime.now().minusDays(5)).end(LocalDateTime.now()
                .minusDays(4)).booker(null).status(BookingStatus.APPROVED).build();
        Booking next = Booking.builder().id(502L).start(LocalDateTime.now().plusDays(1)).end(LocalDateTime.now()
                .plusDays(2)).booker(null).status(BookingStatus.APPROVED).build();

        when(itemRepo.findById(itemId)).thenReturn(Optional.of(itemLocal));
        when(commentRepo.findByItem_IdOrderByCreatedDesc(itemId)).thenReturn(List.of());
        when(bookingRepo.findFirstByItem_IdAndStatusAndStartBeforeOrderByStartDesc(eq(itemId),
                eq(BookingStatus.APPROVED), any())).thenReturn(Optional.of(last));
        when(bookingRepo.findFirstByItem_IdAndStatusAndStartAfterOrderByStartAsc(eq(itemId),
                eq(BookingStatus.APPROVED), any())).thenReturn(Optional.of(next));

        Mockito.doAnswer(invocation -> {
            Item i = invocation.getArgument(0);
            BookingShortDto lastDto = invocation.getArgument(1);
            BookingShortDto nextDto = invocation.getArgument(2);
            List<CommentDto> comments = invocation.getArgument(3);
            ItemResponseDto r = new ItemResponseDto();
            r.setId(i.getId());
            r.setName(i.getName());
            r.setDescription(i.getDescription());
            r.setAvailable(i.getAvailable());
            r.setLastBooking(lastDto);
            r.setNextBooking(nextDto);
            r.setComments(comments);
            return r;
        }).when(itemMapper).toResponseDto(any(), any(), any(), any());

        ItemResponseDto response = itemService.getById(requesterId, itemId);

        assertNotNull(response);
        assertNotNull(response.getLastBooking());
        assertEquals(501L, response.getLastBooking().getId());
        assertNull(response.getLastBooking().getBookerId());
        assertNotNull(response.getNextBooking());
        assertEquals(502L, response.getNextBooking().getId());
        assertNull(response.getNextBooking().getBookerId());

        verify(itemRepo).findById(itemId);
        verify(bookingRepo).findFirstByItem_IdAndStatusAndStartBeforeOrderByStartDesc(eq(itemId),
                eq(BookingStatus.APPROVED), any());
        verify(bookingRepo).findFirstByItem_IdAndStatusAndStartAfterOrderByStartAsc(eq(itemId),
                eq(BookingStatus.APPROVED), any());
    }

    @Test
    void getByOwner_Success_and_pagination_and_bookingsMissing() {
        Long ownerId = 4L;
        Item item1 = Item.builder().id(101L).name("a").description("d").available(true).owner(User.builder().id(ownerId).build()).build();
        Item item2 = Item.builder().id(102L).name("b").description("d2").available(false).owner(User.builder().id(ownerId).build()).build();

        when(userRepo.findById(ownerId)).thenReturn(Optional.of(User.builder().id(ownerId).build()));
        when(itemRepo.findAllByOwner_Id(eq(ownerId), any(PageRequest.class))).thenReturn(new PageImpl<>(List.of(item1, item2)));
        when(bookingRepo.findFirstByItem_IdAndStatusAndStartBeforeOrderByStartDesc(anyLong(),
                eq(BookingStatus.APPROVED), any())).thenReturn(Optional.empty());
        when(bookingRepo.findFirstByItem_IdAndStatusAndStartAfterOrderByStartAsc(anyLong(),
                eq(BookingStatus.APPROVED), any())).thenReturn(Optional.empty());
        when(commentRepo.findByItem_IdOrderByCreatedDesc(anyLong())).thenReturn(List.of());
        Mockito.doAnswer(invocation -> {
            Item i = invocation.getArgument(0);
            ItemResponseDto r = new ItemResponseDto();
            r.setId(i.getId());
            r.setName(i.getName());
            return r;
        }).when(itemMapper).toResponseDto(any(), any(), any(), any());

        var result = itemService.getByOwner(ownerId, 0, 10);

        assertEquals(2, result.size());
        verify(itemRepo).findAllByOwner_Id(eq(ownerId), any(PageRequest.class));
        verify(itemMapper, times(2)).toResponseDto(any(), any(), any(), any());
    }

    @Test
    void getByOwner_lastNextNull_andNonNullHandled() {
        User owner = User.builder().id(5L).name("Owner").email("o@e").build();
        Item itemLocal = Item.builder().id(50L).name("Hammer").description("H").available(true).owner(owner).build();

        when(userRepo.findById(owner.getId())).thenReturn(Optional.of(owner));
        when(itemRepo.findAllByOwner_Id(eq(owner.getId()), any())).thenReturn(new PageImpl<>(List.of(itemLocal)));
        when(commentRepo.findByItem_IdOrderByCreatedDesc(itemLocal.getId())).thenReturn(Collections.emptyList());

        when(bookingRepo.findFirstByItem_IdAndStatusAndStartBeforeOrderByStartDesc(eq(itemLocal.getId()),
                eq(BookingStatus.APPROVED), any())).thenReturn(Optional.empty());
        when(bookingRepo.findFirstByItem_IdAndStatusAndStartAfterOrderByStartAsc(eq(itemLocal.getId()),
                eq(BookingStatus.APPROVED), any())).thenReturn(Optional.empty());

        when(itemMapper.toResponseDto(eq(itemLocal), any(), any(), any())).thenReturn(new ItemResponseDto());
        List<ItemResponseDto> res1 = itemService.getByOwner(owner.getId(), 0, 10);
        assertEquals(1, res1.size());

        Booking last = Booking.builder().id(301L).booker(User.builder().id(6L).build()).start(LocalDateTime.now()
                .minusDays(2)).end(LocalDateTime.now().minusDays(1)).status(BookingStatus.APPROVED).build();
        Booking next = Booking.builder().id(302L).booker(User.builder().id(6L).build()).start(LocalDateTime.now()
                .plusDays(1)).end(LocalDateTime.now().plusDays(2)).status(BookingStatus.APPROVED).build();

        when(bookingRepo.findFirstByItem_IdAndStatusAndStartBeforeOrderByStartDesc(eq(itemLocal.getId()),
                eq(BookingStatus.APPROVED), any())).thenReturn(Optional.of(last));
        when(bookingRepo.findFirstByItem_IdAndStatusAndStartAfterOrderByStartAsc(eq(itemLocal.getId()),
                eq(BookingStatus.APPROVED), any())).thenReturn(Optional.of(next));

        List<ItemResponseDto> res2 = itemService.getByOwner(owner.getId(), 0, 10);
        assertEquals(1, res2.size());
        verify(itemMapper, atLeast(2)).toResponseDto(eq(itemLocal), any(), any(), any());
    }

    @Test
    void getByOwner_fromNegative_treatedAsZero_and_InvalidSize_throws() {
        when(userRepo.findById(1L)).thenReturn(Optional.of(user));
        when(itemRepo.findAllByOwner_Id(eq(1L), any(PageRequest.class))).thenReturn(new PageImpl<>(List.of()));

        itemService.getByOwner(1L, -5, 5);

        assertThrows(BadRequestException.class, () -> itemService.getByOwner(1L, 0, 0));
    }

    @Test
    void search_blankText_returnsEmpty_withoutRepoCall() {
        List<ItemDto> r1 = itemService.search(null, 0, 10);
        List<ItemDto> r2 = itemService.search("   ", 0, 10);
        assertTrue(r1.isEmpty());
        assertTrue(r2.isEmpty());
        verifyNoInteractions(itemRepo);
    }

    @Test
    void search_negativeFrom_treatedAsZero_and_sizeZero_throws() {
        assertThrows(BadRequestException.class, () -> itemService.search("x", 0, 0));
        when(itemRepo.search(eq("x"), any(PageRequest.class))).thenReturn(List.of());
        var res = itemService.search("x", -20, 5);
        assertTrue(res.isEmpty());
        verify(itemRepo).search(eq("x"), any(PageRequest.class));
    }

    @Test
    void search_Success_mapsToDto() {
        when(itemRepo.search(eq("test"), any(PageRequest.class))).thenReturn(List.of(item));
        when(itemMapper.toDto(item)).thenReturn(itemDto);
        List<ItemDto> result = itemService.search("test", 0, 10);
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void addComment_authorNotFound_throwsNotFound() {
        when(userRepo.findById(999L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> itemService.addComment(999L, item.getId(),
                CommentDto.builder().text("x").build()));
        verify(userRepo).findById(999L);
        verifyNoInteractions(bookingRepo);
        verifyNoInteractions(commentRepo);
        verify(itemRepo, never()).findById(anyLong());
    }

    @Test
    void addComment_itemNotFound_throwsNotFound() {
        User booker = User.builder().id(6L).name("B").email("b@e").build();
        when(userRepo.findById(booker.getId())).thenReturn(Optional.of(booker));
        when(itemRepo.findById(item.getId())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> itemService.addComment(booker.getId(), item.getId(),
                CommentDto.builder().text("ok").build()));
    }

    @Test
    void addComment_noCompletedBooking_throwsBadRequest() {
        Long authorId = 7L;
        Long itemId = 77L;
        CommentDto dto = CommentDto.builder().text("ok").build();
        when(userRepo.findById(authorId)).thenReturn(Optional.of(User.builder().id(authorId).build()));
        when(itemRepo.findById(itemId)).thenReturn(Optional.of(Item.builder().id(itemId).build()));
        when(bookingRepo.findByBooker_IdAndItem_IdAndStatusAndEndBefore(eq(authorId), eq(itemId),
                eq(BookingStatus.APPROVED), any())).thenReturn(List.of());

        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> itemService.addComment(authorId, itemId, dto));
        assertTrue(ex.getMessage().toLowerCase().contains("completed") ||
                ex.getMessage().toLowerCase().contains("hasn't"));
    }

    @Test
    void addComment_success_setsCreatedAndAuthor() {
        Long authorId = 8L;
        Long itemId = 88L;
        CommentDto dto = CommentDto.builder().text("nice").build();
        User author = User.builder().id(authorId).name("Au").build();
        when(userRepo.findById(authorId)).thenReturn(Optional.of(author));
        when(itemRepo.findById(itemId)).thenReturn(Optional.of(Item.builder().id(itemId).build()));
        when(bookingRepo.findByBooker_IdAndItem_IdAndStatusAndEndBefore(eq(authorId), eq(itemId),
                eq(BookingStatus.APPROVED),
                any())).thenReturn(List.of(Booking.builder().id(1L).end(LocalDateTime.now().minusDays(1)).
                status(BookingStatus.APPROVED).build()));

        Comment toSave = Comment.builder().id(999L).text(dto.getText()).build();
        when(commentMapper.toModel(dto)).thenReturn(Comment.builder().text(dto.getText()).build());
        when(commentRepo.save(any())).thenReturn(toSave);
        when(commentMapper.toDto(toSave)).thenReturn(CommentDto.builder().id(999L).itemId(itemId).authorId(authorId)
                .text(dto.getText()).authorName("Au").build());

        CommentDto result = itemService.addComment(authorId, itemId, dto);

        assertNotNull(result);
        assertEquals(999L, result.getId());
        assertEquals("Au", result.getAuthorName());
        verify(commentRepo).save(any());
    }

    @Test
    void addComment_success_whenCompletedBookingExists_mapperAndSave() {
        User booker = User.builder().id(6L).name("B").email("b@e").build();
        Item itemLocal = Item.builder().id(50L).build();
        Booking b = Booking.builder().id(900L).booker(booker).item(itemLocal).status(BookingStatus.APPROVED)
                .end(LocalDateTime.now().minusDays(1)).build();

        when(userRepo.findById(booker.getId())).thenReturn(Optional.of(booker));
        when(itemRepo.findById(itemLocal.getId())).thenReturn(Optional.of(itemLocal));
        when(bookingRepo.findByBooker_IdAndItem_IdAndStatusAndEndBefore(eq(booker.getId()), eq(itemLocal.getId()),
                eq(BookingStatus.APPROVED), any())).thenReturn(List.of(b));
        when(commentMapper.toModel(commentDto)).thenReturn(Comment.builder().text(commentDto.getText()).build());
        when(commentRepo.save(any())).thenReturn(comment);
        when(commentMapper.toDto(comment)).thenReturn(CommentDto.builder().id(comment.getId()).text(comment.getText())
                .authorId(booker.getId()).authorName(booker.getName()).itemId(itemLocal.getId()).created(comment
                        .getCreated()).build());

        CommentDto res = itemService.addComment(booker.getId(), itemLocal.getId(), commentDto);
        assertNotNull(res);
        assertEquals(comment.getText(), res.getText());
        verify(commentRepo).save(any(Comment.class));
    }

    @Test
    void getByRequestId_Success_and_whenNoItems_returnsEmptyList() {
        when(itemRepo.findByRequestId(1L)).thenReturn(List.of(item));
        when(itemMapper.toDto(item)).thenReturn(itemDto);

        List<ItemDto> result = itemService.getByRequestId(1L);

        assertNotNull(result);
        assertEquals(1, result.size());

        when(itemRepo.findByRequestId(123L)).thenReturn(Collections.emptyList());
        List<ItemDto> resEmpty = itemService.getByRequestId(123L);
        assertNotNull(resEmpty);
        assertTrue(resEmpty.isEmpty());
    }
}