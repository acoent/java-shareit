package ru.practicum.shareit.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

public abstract class BaseClient {
    protected final RestTemplate rest;
    protected final String serverUrl;

    public BaseClient(RestTemplate rest, @Value("${shareit.server.url}") String serverUrl) {
        this.rest = rest;
        this.serverUrl = serverUrl;
    }

    protected ResponseEntity<Object> get(String path, Map<String, Object> parameters) {
        return makeAndSendRequest(HttpMethod.GET, path, parameters, null);
    }

    protected ResponseEntity<Object> post(String path, Object body) {
        return makeAndSendRequest(HttpMethod.POST, path, null, body);
    }

    protected ResponseEntity<Object> put(String path, Object body) {
        return makeAndSendRequest(HttpMethod.PUT, path, null, body);
    }

    protected ResponseEntity<Object> patch(String path, Object body) {
        return makeAndSendRequest(HttpMethod.PATCH, path, null, body);
    }

    protected ResponseEntity<Object> delete(String path) {
        return makeAndSendRequest(HttpMethod.DELETE, path, null, null);
    }

    private ResponseEntity<Object> makeAndSendRequest(HttpMethod method, String path, Map<String, Object> parameters, Object body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Object> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<Object> shareItServerResponse;

        if (parameters != null) {
            shareItServerResponse = rest.exchange(serverUrl + path, method, requestEntity, Object.class, parameters);
        } else {
            shareItServerResponse = rest.exchange(serverUrl + path, method, requestEntity, Object.class);
        }

        return shareItServerResponse;
    }

    protected ResponseEntity<Object> get(String path, Map<String, Object> parameters, Long userId) {
        return makeAndSendRequest(HttpMethod.GET, path, parameters, null, userId);
    }

    protected ResponseEntity<Object> post(String path, Object body, Long userId) {
        return makeAndSendRequest(HttpMethod.POST, path, null, body, userId);
    }

    protected ResponseEntity<Object> put(String path, Object body, Long userId) {
        return makeAndSendRequest(HttpMethod.PUT, path, null, body, userId);
    }

    protected ResponseEntity<Object> patch(String path, Object body, Long userId) {
        return makeAndSendRequest(HttpMethod.PATCH, path, null, body, userId);
    }

    protected ResponseEntity<Object> delete(String path, Long userId) {
        return makeAndSendRequest(HttpMethod.DELETE, path, null, null, userId);
    }

    private ResponseEntity<Object> makeAndSendRequest(HttpMethod method, String path, Map<String, Object> parameters, Object body, Long userId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (userId != null) {
            headers.set("X-Sharer-User-Id", userId.toString());
        }

        HttpEntity<Object> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<Object> shareItServerResponse;

        if (parameters != null) {
            shareItServerResponse = rest.exchange(serverUrl + path, method, requestEntity, Object.class, parameters);
        } else {
            shareItServerResponse = rest.exchange(serverUrl + path, method, requestEntity, Object.class);
        }

        return shareItServerResponse;
    }
}
