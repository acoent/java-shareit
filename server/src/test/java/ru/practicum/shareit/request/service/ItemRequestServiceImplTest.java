package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
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
class ItemRequestServiceImplTest {

    @Mock
    private ItemRequestRepository itemRequestRepo;

    @Mock
    private UserRepository userRepo;

    @Mock
    private ItemService itemService;

    @Mock
    private ItemRequestMapper itemRequestMapper;

    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;

    private User user;
    private ItemRequest itemRequest;
    private ItemRequestDto itemRequestDto;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .name("John Doe")
                .email("john@example.com")
                .build();

        itemRequest = ItemRequest.builder()
                .id(1L)
                .description("Need a drill")
                .requesterId(1L)
                .created(LocalDateTime.now())
                .build();

        itemRequestDto = ItemRequestDto.builder()
                .id(1L)
                .description("Need a drill")
                .requesterId(1L)
                .created(LocalDateTime.now())
                .items(Collections.emptyList())
                .build();
    }

    @Test
    void create_Success() {
        ItemRequestDto incomingDto = ItemRequestDto.builder()
                .description(itemRequestDto.getDescription())
                .build();

        when(userRepo.findById(1L)).thenReturn(Optional.of(user));
        when(itemRequestMapper.toModel(eq(incomingDto))).thenReturn(ItemRequest.builder().description(incomingDto.getDescription()).build());
        when(itemRequestRepo.save(any(ItemRequest.class))).thenReturn(itemRequest);
        when(itemRequestMapper.toDto(itemRequest)).thenReturn(itemRequestDto);

        ItemRequestDto result = itemRequestService.create(1L, incomingDto);

        assertNotNull(result);
        assertEquals(itemRequestDto.getDescription(), result.getDescription());
        verify(itemRequestRepo).save(any(ItemRequest.class));
        verify(itemRequestMapper).toModel(incomingDto);
        verify(itemRequestMapper).toDto(itemRequest);
    }

    @Test
    void create_UserNotFound_ThrowsNotFoundException() {
        when(userRepo.findById(1L)).thenReturn(Optional.empty());
        ItemRequestDto incomingDto = ItemRequestDto.builder().description("Need a drill").build();
        assertThrows(NotFoundException.class, () -> itemRequestService.create(1L, incomingDto));
        verify(userRepo).findById(1L);
        verifyNoInteractions(itemRequestRepo, itemRequestMapper, itemService);
    }

    @Test
    void getByRequester_Success() {
        when(userRepo.findById(1L)).thenReturn(Optional.of(user));
        when(itemRequestRepo.findByRequesterIdOrderByCreatedDesc(1L)).thenReturn(List.of(itemRequest));
        when(itemService.getByRequestId(1L)).thenReturn(Collections.emptyList());
        when(itemRequestMapper.toDto(itemRequest)).thenReturn(itemRequestDto);

        List<ItemRequestDto> result = itemRequestService.getByRequester(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(itemRequestDto.getId(), result.get(0).getId());

        verify(userRepo).findById(1L);
        verify(itemRequestRepo).findByRequesterIdOrderByCreatedDesc(1L);
        verify(itemRequestMapper).toDto(itemRequest);
        verify(itemService).getByRequestId(1L);
    }

    @Test
    void getByRequester_UserNotFound_ThrowsNotFoundException() {
        when(userRepo.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemRequestService.getByRequester(1L));
        verify(userRepo).findById(1L);
        verifyNoInteractions(itemRequestRepo, itemService, itemRequestMapper);
    }

    @Test
    void getAll_Success() {
        Page<ItemRequest> page = new PageImpl<>(List.of(itemRequest));
        when(userRepo.findById(2L)).thenReturn(Optional.of(user));
        when(itemRequestRepo.findByRequesterIdNot(eq(2L), any(PageRequest.class))).thenReturn(page);
        when(itemService.getByRequestId(1L)).thenReturn(Collections.emptyList());
        when(itemRequestMapper.toDto(itemRequest)).thenReturn(itemRequestDto);

        List<ItemRequestDto> result = itemRequestService.getAll(2L, 0, 10);

        assertNotNull(result);
        assertEquals(1, result.size());

        verify(userRepo).findById(2L);
        verify(itemRequestRepo).findByRequesterIdNot(eq(2L), any(PageRequest.class));
        verify(itemRequestMapper).toDto(itemRequest);
        verify(itemService).getByRequestId(1L);
    }

    @Test
    void getAll_InvalidSize_ThrowsIllegalArgumentException() {
        when(userRepo.findById(1L)).thenReturn(Optional.of(user));
        assertThrows(IllegalArgumentException.class, () -> itemRequestService.getAll(1L, 0, 0));
        verify(userRepo).findById(1L);
        verifyNoInteractions(itemRequestRepo, itemRequestMapper, itemService);
    }

    @Test
    void getById_Success() {
        when(userRepo.findById(1L)).thenReturn(Optional.of(user));
        when(itemRequestRepo.findById(1L)).thenReturn(Optional.of(itemRequest));
        when(itemService.getByRequestId(1L)).thenReturn(Collections.emptyList());
        when(itemRequestMapper.toDto(itemRequest)).thenReturn(itemRequestDto);

        ItemRequestDto result = itemRequestService.getById(1L, 1L);

        assertNotNull(result);
        assertEquals(itemRequestDto.getId(), result.getId());

        verify(userRepo).findById(1L);
        verify(itemRequestRepo).findById(1L);
        verify(itemRequestMapper).toDto(itemRequest);
        verify(itemService).getByRequestId(1L);
    }

    @Test
    void getById_RequestNotFound_ThrowsNotFoundException() {
        when(userRepo.findById(1L)).thenReturn(Optional.of(user));
        when(itemRequestRepo.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemRequestService.getById(1L, 1L));
        verify(userRepo).findById(1L);
        verify(itemRequestRepo).findById(1L);
        verifyNoMoreInteractions(itemRequestMapper, itemService);
    }
}
