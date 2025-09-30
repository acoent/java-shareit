package ru.practicum.shareit.client;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.dto.UserDto;

@Service
public class UserClient extends BaseClient {

    public UserClient(RestTemplate restTemplate) {
        super(restTemplate);
    }

    public ResponseEntity<Object> createUser(UserDto userDto) {
        return post("/users", userDto, null);
    }

    public ResponseEntity<Object> updateUser(Long userId, UserDto userDto) {
        return patch("/users/" + userId, userDto, userId);
    }

    public ResponseEntity<Object> getUser(Long userId) {
        return get("/users/" + userId, null, userId);
    }

    public ResponseEntity<Object> getAllUsers() {
        return get("/users", null, null);
    }

    public ResponseEntity<Object> deleteUser(Long userId) {
        return delete("/users/" + userId, userId);
    }
}