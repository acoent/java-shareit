package ru.practicum.shareit.item.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
public class InMemoryItemRepository {
    private final Map<Long, Item> items = new ConcurrentHashMap<>();
    private final AtomicLong nextId = new AtomicLong(1);

    public Item save(Item item) {
        if (item.getId() == null) item.setId(nextId.getAndIncrement());
        items.put(item.getId(), item);
        return item;
    }

    public Optional<Item> findById(Long id) {
        return Optional.ofNullable(items.get(id));
    }

    public List<Item> findAllByOwner(Long ownerId) {
        return items.values().stream().filter(i -> i.getOwnerId().equals(ownerId)).sorted(Comparator.comparing(Item::getId)).collect(Collectors.toList());
    }

    public List<Item> searchAvailableByText(String text) {
        String q = text.toLowerCase();
        return items.values().stream().filter(i -> Boolean.TRUE.equals(i.getAvailable())).filter(i -> (i.getName() != null && i.getName().toLowerCase().contains(q)) || (i.getDescription() != null && i.getDescription().toLowerCase().contains(q))).sorted(Comparator.comparing(Item::getId)).collect(Collectors.toList());
    }

    public List<Item> findAll() {
        return new ArrayList<>(items.values());
    }
}
