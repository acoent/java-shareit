package ru.practicum.shareit.request.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.InMemoryItemRequestRepository;
import ru.practicum.shareit.user.repository.InMemoryUserRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.BadRequestException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ItemRequestServiceImpl implements ItemRequestService {

    private final InMemoryItemRequestRepository repo;
    private final InMemoryUserRepository userRepo;

    public ItemRequestServiceImpl(InMemoryItemRequestRepository repo, InMemoryUserRepository userRepo) {
        this.repo = repo;
        this.userRepo = userRepo;
    }

    @Override
    public ItemRequestDto create(Long userId, ItemRequestDto dto) {
        if (userId == null || !userRepo.existsById(userId)) throw new NotFoundException("User not found: " + userId);
        if (dto == null || dto.getDescription() == null || dto.getDescription().isBlank())
            throw new BadRequestException("Description required");
        ItemRequest r = new ItemRequest();
        r.setDescription(dto.getDescription());
        r.setRequesterId(userId);
        r.setCreated(LocalDateTime.now());
        ItemRequest saved = repo.save(r);
        ItemRequestDto out = new ItemRequestDto();
        out.setId(saved.getId());
        out.setDescription(saved.getDescription());
        out.setRequesterId(saved.getRequesterId());
        out.setCreated(saved.getCreated());
        return out;
    }

    @Override
    public List<ItemRequestDto> getOwn(Long userId) {
        if (!userRepo.existsById(userId)) throw new NotFoundException("User not found: " + userId);
        return repo.findByRequester(userId).stream().map(r -> {
            ItemRequestDto d = new ItemRequestDto();
            d.setId(r.getId());
            d.setDescription(r.getDescription());
            d.setRequesterId(r.getRequesterId());
            d.setCreated(r.getCreated());
            return d;
        }).collect(Collectors.toList());
    }
}
