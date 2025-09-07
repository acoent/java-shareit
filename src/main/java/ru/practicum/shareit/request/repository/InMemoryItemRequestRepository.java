package ru.practicum.shareit.request.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
public class InMemoryItemRequestRepository {

    private final Map<Long, ItemRequest> storage = new ConcurrentHashMap<>();
    private final AtomicLong seq = new AtomicLong(1);

    public ItemRequest save(ItemRequest r) {
        if (r.getId() == null) r.setId(seq.getAndIncrement());
        storage.put(r.getId(), r);
        return r;
    }

    public Optional<ItemRequest> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    public List<ItemRequest> findByRequesterId(Long requesterId) {
        return storage.values().stream()
                .filter(r -> Objects.equals(r.getRequesterId(), requesterId))
                .sorted(Comparator.comparing(ItemRequest::getCreated).reversed())
                .collect(Collectors.toList());
    }

    public List<ItemRequest> findAll() {
        return storage.values().stream()
                .sorted(Comparator.comparing(ItemRequest::getCreated).reversed())
                .collect(Collectors.toList());
    }
}
