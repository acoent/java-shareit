package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InMemoryUserRepository {
    private final Map<Long, User> users = new ConcurrentHashMap<>();
    private final AtomicLong nextId = new AtomicLong(1);

    public User save(User user) {
        if (user.getId() == null) user.setId(nextId.getAndIncrement());
        users.put(user.getId(), user);
        return user;
    }

    public Optional<User> findById(Long id) {
        return Optional.ofNullable(users.get(id));
    }

    public void deleteById(Long id) {
        users.remove(id);
    }

    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    public boolean existsById(Long id) {
        return users.containsKey(id);
    }

    public Optional<User> findByEmail(String email) {
        return users.values().stream().filter(u -> u.getEmail() != null && u.getEmail().equals(email)).findFirst();
    }

}
