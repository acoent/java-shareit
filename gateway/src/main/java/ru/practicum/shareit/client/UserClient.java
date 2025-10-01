package ru.practicum.shareit.client;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ru.practicum.shareit.dto.UserDto;

@Service
public class UserClient extends BaseClient {
    private static final String API_PREFIX = "/users";

    public UserClient(RestTemplate restTemplate) {
        super(restTemplate);
    }

    public ResponseEntity<Object> createUser(UserDto userDto) {
        return post(API_PREFIX, userDto, null);
    }

    public ResponseEntity<Object> updateUser(Long userId, UserDto userDto) {
        String path = UriComponentsBuilder.fromPath(API_PREFIX)
                .path("/{userId}")
                .buildAndExpand(userId)
                .toUriString();
        return patch(path, userDto, userId);
    }

    public ResponseEntity<Object> getUser(Long userId) {
        String path = UriComponentsBuilder.fromPath(API_PREFIX)
                .path("/{userId}")
                .buildAndExpand(userId)
                .toUriString();
        return get(path, null, userId);
    }

    public ResponseEntity<Object> getAllUsers() {
        return get(API_PREFIX, null, null);
    }

    public ResponseEntity<Object> deleteUser(Long userId) {
        String path = UriComponentsBuilder.fromPath(API_PREFIX)
                .path("/{userId}")
                .buildAndExpand(userId)
                .toUriString();
        return delete(path, userId);
    }
}