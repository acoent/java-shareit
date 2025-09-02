package ru.practicum.shareit.request.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.request.model.ItemRequest;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
public class InMemoryItemRequestRepository {
    private final Map<Long, ItemRequest> requests = new ConcurrentHashMap<>();
    private final AtomicLong nextId = new AtomicLong(1);

    public ItemRequest save(ItemRequest r) {
        if (r.getId() == null) r.setId(nextId.getAndIncrement());
        if (r.getCreated() == null) r.setCreated(LocalDateTime.now());
        requests.put(r.getId(), r);
        return r;
    }

    public Optional<ItemRequest> findById(Long id) { return Optional.ofNullable(requests.get(id)); }
    public List<ItemRequest> findByRequester(Long requesterId) {
        return requests.values().stream().filter(r -> r.getRequesterId().equals(requesterId)).sorted(Comparator.comparing(ItemRequest::getCreated).reversed()).collect(Collectors.toList());
    }

    public List<ItemRequest> findAll() {
        return new ArrayList<>(requests.values());
    }
}
