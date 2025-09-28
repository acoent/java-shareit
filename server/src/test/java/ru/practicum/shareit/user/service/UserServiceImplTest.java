package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepo;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .name("John Doe")
                .email("john@example.com")
                .build();

        userDto = UserDto.builder()
                .id(1L)
                .name("John Doe")
                .email("john@example.com")
                .build();
    }

    @Test
    void create_Success() {
        when(userRepo.findByEmailIgnoreCase(userDto.getEmail())).thenReturn(Optional.empty());
        when(userMapper.toModel(userDto)).thenReturn(user);
        when(userRepo.save(any(User.class))).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(userDto);

        UserDto result = userService.create(userDto);

        assertNotNull(result);
        assertEquals(userDto.getName(), result.getName());
        assertEquals(userDto.getEmail(), result.getEmail());
        verify(userRepo).save(any(User.class));
    }

    @Test
    void create_NullDto_ThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> userService.create(null));
    }

    @Test
    void create_EmailExists_ThrowsConflictException() {
        when(userRepo.findByEmailIgnoreCase(userDto.getEmail())).thenReturn(Optional.of(user));

        assertThrows(ConflictException.class, () -> userService.create(userDto));
    }

    @Test
    void update_Success() {
        UserDto updateDto = UserDto.builder()
                .name("Updated Name")
                .email("updated@example.com")
                .build();

        when(userRepo.findById(1L)).thenReturn(Optional.of(user));
        when(userRepo.findByEmailIgnoreCase(updateDto.getEmail())).thenReturn(Optional.empty());
        when(userRepo.save(any(User.class))).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(userDto);

        UserDto result = userService.update(1L, updateDto);

        assertNotNull(result);
        verify(userRepo).save(any(User.class));
    }

    @Test
    void update_NullDto_ThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> userService.update(1L, null));
    }

    @Test
    void update_UserNotFound_ThrowsNotFoundException() {
        when(userRepo.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.update(1L, userDto));
    }

    @Test
    void update_EmailConflict_ThrowsConflictException() {
        User existingUser = User.builder().id(2L).email("test@example.com").build();
        UserDto updateDto = UserDto.builder().email("test@example.com").build();

        when(userRepo.findById(1L)).thenReturn(Optional.of(user));
        when(userRepo.findByEmailIgnoreCase("test@example.com")).thenReturn(Optional.of(existingUser));

        assertThrows(ConflictException.class, () -> userService.update(1L, updateDto));
    }

    @Test
    void getById_Success() {
        when(userRepo.findById(1L)).thenReturn(Optional.of(user));
        when(userMapper.toDto(user)).thenReturn(userDto);

        UserDto result = userService.getById(1L);

        assertNotNull(result);
        assertEquals(userDto.getId(), result.getId());
    }

    @Test
    void getById_UserNotFound_ThrowsNotFoundException() {
        when(userRepo.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.getById(1L));
    }

    @Test
    void getAll_Success() {
        when(userRepo.findAll()).thenReturn(List.of(user));
        when(userMapper.toDto(user)).thenReturn(userDto);

        List<UserDto> result = userService.getAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(userDto.getId(), result.get(0).getId());
    }

    @Test
    void delete_Success() {
        when(userRepo.existsById(1L)).thenReturn(true);
        doNothing().when(userRepo).deleteById(1L);

        assertDoesNotThrow(() -> userService.delete(1L));
        verify(userRepo).deleteById(1L);
    }

    @Test
    void delete_UserNotFound_ThrowsNotFoundException() {
        when(userRepo.existsById(1L)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> userService.delete(1L));
    }
}
