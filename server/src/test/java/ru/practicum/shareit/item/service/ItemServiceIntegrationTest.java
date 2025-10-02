package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
@TestPropertySource(properties = "db.name=test")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemServiceIntegrationTest {

    private final ItemService itemService;
    private final UserService userService;

    @Test
    void getUserItems_ReturnsAllUserItems() {
        UserDto user = UserDto.builder()
                .name("Test User")
                .email("test@example.com")
                .build();
        UserDto savedUser = userService.create(user);

        ItemDto item1 = ItemDto.builder()
                .name("Item 1")
                .description("Description 1")
                .available(true)
                .build();

        ItemDto item2 = ItemDto.builder()
                .name("Item 2")
                .description("Description 2")
                .available(true)
                .build();

        itemService.create(savedUser.getId(), item1);
        itemService.create(savedUser.getId(), item2);

        List<ItemResponseDto> items = itemService.getByOwner(savedUser.getId(), 0, 10);

        assertEquals(2, items.size());
    }
}