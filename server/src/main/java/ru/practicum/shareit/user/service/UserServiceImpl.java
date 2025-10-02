package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepo;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public UserDto create(UserDto dto) {
        if (dto == null) {
            throw new IllegalArgumentException("UserDto must not be null");
        }
        userRepo.findByEmailIgnoreCase(dto.getEmail()).ifPresent(u -> {
            throw new ConflictException("Email already in use");
        });

        User user = userMapper.toModel(dto);
        User saved = userRepo.save(user);
        return userMapper.toDto(saved);
    }

    @Override
    @Transactional
    public UserDto update(Long id, UserDto dto) {
        if (dto == null) {
            throw new IllegalArgumentException("UserDto must not be null");
        }

        User user = userRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found: " + id));

        if (dto.getName() != null && !dto.getName().isBlank()) {
            user.setName(dto.getName());
        }

        if (dto.getEmail() != null && !dto.getEmail().isBlank()) {
            userRepo.findByEmailIgnoreCase(dto.getEmail())
                    .filter(existing -> !existing.getId().equals(id))
                    .ifPresent(existing -> {
                        throw new ConflictException("Email already in use");
                    });
            user.setEmail(dto.getEmail());
        }

        User saved = userRepo.save(user);
        return userMapper.toDto(saved);
    }

    @Override
    public UserDto getById(Long id) {
        return userRepo.findById(id)
                .map(userMapper::toDto)
                .orElseThrow(() -> new NotFoundException("User not found: " + id));
    }

    @Override
    public List<UserDto> getAll() {
        return userRepo.findAll().stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!userRepo.existsById(id)) throw new NotFoundException("User not found: " + id);
        userRepo.deleteById(id);
    }
}
