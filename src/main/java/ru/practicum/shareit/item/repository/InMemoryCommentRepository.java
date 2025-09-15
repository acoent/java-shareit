package ru.practicum.shareit.item.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Comment;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
public class InMemoryCommentRepository {
    private final Map<Long, Comment> storage = new ConcurrentHashMap<>();
    private final AtomicLong seq = new AtomicLong(1);

    public Comment save(Comment comment) {
        if (comment.getId() == null) comment.setId(seq.getAndIncrement());
        storage.put(comment.getId(), comment);
        return comment;
    }

    public List<Comment> findByItemId(Long itemId) {
        return storage.values().stream()
                .filter(c -> Objects.equals(c.getItemId(), itemId))
                .sorted(Comparator.comparing(Comment::getCreated).reversed())
                .collect(Collectors.toList());
    }
}
