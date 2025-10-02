package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository requestRepo;
    private final UserRepository userRepo;
    private final ItemRequestMapper requestMapper;
    private final ItemService itemService;

    @Override
    @Transactional
    public ItemRequestDto create(Long userId, ItemRequestDto dto) {
        userRepo.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found: " + userId));

        ItemRequest request = requestMapper.toModel(dto);
        request.setRequesterId(userId);
        request.setCreated(LocalDateTime.now());

        ItemRequest saved = requestRepo.save(request);
        return requestMapper.toDto(saved);
    }

    @Override
    public List<ItemRequestDto> getByRequester(Long userId) {
        userRepo.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found: " + userId));

        List<ItemRequest> requests = requestRepo.findByRequesterIdOrderByCreatedDesc(userId);
        return requests.stream()
                .map(this::enrichWithItems)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestDto> getAll(Long userId, int from, int size) {
        userRepo.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found: " + userId));

        if (size <= 0) {
            throw new IllegalArgumentException("size must be greater than 0");
        }
        int page = from >= 0 ? from / size : 0;
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "created"));

        Page<ItemRequest> pageResult = requestRepo.findByRequesterIdNot(userId, pageRequest);
        return pageResult.getContent().stream()
                .map(this::enrichWithItems)
                .collect(Collectors.toList());
    }

    @Override
    public ItemRequestDto getById(Long userId, Long requestId) {
        userRepo.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found: " + userId));

        ItemRequest request = requestRepo.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Request not found: " + requestId));

        return enrichWithItems(request);
    }

    private ItemRequestDto enrichWithItems(ItemRequest request) {
        ItemRequestDto dto = requestMapper.toDto(request);
        List<ItemDto> items = itemService.getByRequestId(request.getId());
        dto.setItems(items);
        return dto;
    }
}
