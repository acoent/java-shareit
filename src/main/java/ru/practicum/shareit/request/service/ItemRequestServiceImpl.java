package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.InMemoryItemRequestRepository;
import ru.practicum.shareit.user.repository.InMemoryUserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final InMemoryItemRequestRepository requestRepo;
    private final InMemoryUserRepository userRepo;
    private final ItemRequestMapper requestMapper;

    @Override
    public ItemRequestDto create(Long userId, ItemRequestDto dto) {
        if (!userRepo.existsById(userId)) throw new NotFoundException("User not found: " + userId);
        if (dto == null || dto.getDescription() == null || dto.getDescription().isBlank()) {
            throw new BadRequestException("Description required");
        }

        ItemRequest request = requestMapper.toModel(dto);
        request.setRequesterId(userId);
        request.setCreated(LocalDateTime.now());

        request = requestRepo.save(request);
        return requestMapper.toDto(request);
    }

    @Override
    public List<ItemRequestDto> getByRequester(Long userId) {
        if (!userRepo.existsById(userId)) throw new NotFoundException("User not found: " + userId);

        return requestRepo.findByRequesterId(userId).stream()
                .map(requestMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestDto> getAll(Long userId, int from, int size) {
        if (!userRepo.existsById(userId)) throw new NotFoundException("User not found: " + userId);
        if (from < 0) throw new BadRequestException("from must be >= 0");
        if (size <= 0) throw new BadRequestException("size must be > 0");

        return requestRepo.findAll().stream()
                .filter(r -> !r.getRequesterId().equals(userId))
                .skip(from).limit(size)
                .map(requestMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemRequestDto getById(Long userId, Long requestId) {
        if (!userRepo.existsById(userId)) throw new NotFoundException("User not found: " + userId);

        ItemRequest request = requestRepo.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Request not found: " + requestId));

        return requestMapper.toDto(request);
    }
}
