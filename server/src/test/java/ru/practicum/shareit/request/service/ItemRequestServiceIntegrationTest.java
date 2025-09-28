package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@TestPropertySource(properties = "db.name=test")
class ItemRequestServiceIntegrationTest {

    @Autowired
    private ItemRequestService itemRequestService;

    @Autowired
    private UserService userService;

    @Test
    void createAndRetrieveItemRequest() {
        UserDto user = UserDto.builder()
                .name("Test User")
                .email("test@example.com")
                .build();
        UserDto savedUser = userService.create(user);

        ItemRequestDto request = ItemRequestDto.builder()
                .description("Need a drill")
                .build();

        ItemRequestDto savedRequest = itemRequestService.create(savedUser.getId(), request);

        assertNotNull(savedRequest.getId());
        assertEquals("Need a drill", savedRequest.getDescription());

        List<ItemRequestDto> ownRequests = itemRequestService.getByRequester(savedUser.getId());
        assertEquals(1, ownRequests.size());
        assertEquals(savedRequest.getId(), ownRequests.get(0).getId());
    }
}
