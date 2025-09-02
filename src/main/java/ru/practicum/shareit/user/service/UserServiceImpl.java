package ru.practicum.shareit.user.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.InMemoryUserRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.ConflictException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    private final InMemoryUserRepository repo;

    public UserServiceImpl(InMemoryUserRepository repo) {
        this.repo = repo;
    }

    @Override
    public UserDto create(UserDto dto) {
        validate(dto);
        repo.findByEmail(dto.getEmail()).ifPresent(u -> {
            throw new ConflictException("Email already exists: " + dto.getEmail());
        });
        User u = UserMapper.toModel(dto);
        u.setId(null);
        User saved = repo.save(u);
        return UserMapper.toDto(saved);
    }

    @Override
    public UserDto update(Long id, UserDto dto) {
        User existing = repo.findById(id).orElseThrow(() -> new NotFoundException("User not found: " + id));
        if (dto.getName() != null) existing.setName(dto.getName());
        if (dto.getEmail() != null) {
            repo.findByEmail(dto.getEmail()).ifPresent(u -> {
                if (!u.getId().equals(id)) throw new ConflictException("Email already exists: " + dto.getEmail());
            });
            existing.setEmail(dto.getEmail());
        }
        validate(UserMapper.toDto(existing));
        repo.save(existing);
        return UserMapper.toDto(existing);
    }

    @Override
    public UserDto getById(Long id) {
        return UserMapper.toDto(repo.findById(id).orElseThrow(() -> new NotFoundException("User not found: " + id)));
    }

    @Override
    public List<UserDto> getAll() {
        return repo.findAll().stream().map(UserMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public void delete(Long id) {
        repo.deleteById(id);
    }

    private void validate(UserDto dto) {
        if (dto.getName() == null || dto.getName().isBlank()) throw new BadRequestException("Name is required");
        if (dto.getEmail() == null || dto.getEmail().isBlank() || !dto.getEmail().contains("@"))
            throw new BadRequestException("Valid email is required");
    }
}
