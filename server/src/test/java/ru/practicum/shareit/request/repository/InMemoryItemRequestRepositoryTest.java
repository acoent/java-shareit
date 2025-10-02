package ru.practicum.shareit.request.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.request.model.ItemRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryItemRequestRepositoryTest {

    private InMemoryItemRequestRepository repository;
    private ItemRequest itemRequest;

    @BeforeEach
    void setUp() {
        repository = new InMemoryItemRequestRepository();

        itemRequest = ItemRequest.builder()
                .description("Need a drill")
                .requesterId(1L)
                .created(LocalDateTime.now())
                .build();
    }

    @Test
    void save_Success() {
        ItemRequest saved = repository.save(itemRequest);

        assertNotNull(saved.getId());
        assertEquals(itemRequest.getDescription(), saved.getDescription());
        assertEquals(itemRequest.getRequesterId(), saved.getRequesterId());
    }

    @Test
    void findById_Success() {
        ItemRequest saved = repository.save(itemRequest);

        Optional<ItemRequest> found = repository.findById(saved.getId());

        assertTrue(found.isPresent());
        assertEquals(saved.getId(), found.get().getId());
    }

    @Test
    void findById_NotFound() {
        Optional<ItemRequest> found = repository.findById(999L);

        assertFalse(found.isPresent());
    }

    @Test
    void findByRequesterIdOrderByCreatedDesc_Success() {
        repository.save(itemRequest);

        List<ItemRequest> found = repository.findByRequesterId(1L);

        assertEquals(1, found.size());
        assertEquals(itemRequest.getDescription(), found.get(0).getDescription());
        assertEquals(1L, found.get(0).getRequesterId());
    }
}
