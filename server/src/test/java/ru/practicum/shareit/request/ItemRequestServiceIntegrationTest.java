package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ItemRequestServiceIntegrationTest {

    @Autowired
    private ItemRequestService itemRequestService;

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Autowired
    private UserRepository userRepository;

    private User user1;
    private User user2;
    private ItemRequestDto requestDto;

    @BeforeEach
    void setUp() {
        user1 = User.builder()
                .name("User 1")
                .email("user1@example.com")
                .build();
        user1 = userRepository.save(user1);

        user2 = User.builder()
                .name("User 2")
                .email("user2@example.com")
                .build();
        user2 = userRepository.save(user2);

        requestDto = ItemRequestDto.builder()
                .description("Need a drill")
                .build();
    }

    @Test
    void createRequest_ShouldCreateRequestSuccessfully() {
        // When
        ItemRequestDto result = itemRequestService.create(user1.getId(), requestDto);

        // Then
        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals("Need a drill", result.getDescription());
        assertEquals(user1.getId(), result.getRequesterId());
        assertNotNull(result.getCreated());

        // Verify in database
        List<ItemRequest> requests = itemRequestRepository.findAll();
        assertEquals(1, requests.size());
        assertEquals("Need a drill", requests.get(0).getDescription());
    }

    @Test
    void getByRequester_ShouldReturnUserRequests() {
        // Given
        itemRequestService.create(user1.getId(), requestDto);
        ItemRequestDto anotherRequest = ItemRequestDto.builder()
                .description("Need a hammer")
                .build();
        itemRequestService.create(user1.getId(), anotherRequest);

        // When
        List<ItemRequestDto> result = itemRequestService.getByRequester(user1.getId());

        // Then
        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(r -> r.getDescription().equals("Need a drill")));
        assertTrue(result.stream().anyMatch(r -> r.getDescription().equals("Need a hammer")));
    }

    @Test
    void getAll_ShouldReturnOtherUsersRequests() {
        // Given
        itemRequestService.create(user1.getId(), requestDto);
        ItemRequestDto anotherRequest = ItemRequestDto.builder()
                .description("Need a hammer")
                .build();
        itemRequestService.create(user1.getId(), anotherRequest);

        // When
        List<ItemRequestDto> result = itemRequestService.getAll(user2.getId(), 0, 10);

        // Then
        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(r -> r.getDescription().equals("Need a drill")));
        assertTrue(result.stream().anyMatch(r -> r.getDescription().equals("Need a hammer")));
    }

    @Test
    void getById_ShouldReturnSpecificRequest() {
        // Given
        ItemRequestDto created = itemRequestService.create(user1.getId(), requestDto);

        // When
        ItemRequestDto result = itemRequestService.getById(user2.getId(), created.getId());

        // Then
        assertNotNull(result);
        assertEquals(created.getId(), result.getId());
        assertEquals("Need a drill", result.getDescription());
        assertEquals(user1.getId(), result.getRequesterId());
    }
}
