package ru.practicum.shareit.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.*;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.common.HeaderConstants;

import java.util.Map;

public abstract class BaseClient {
    private final ObjectMapper mapper = new ObjectMapper();
    protected RestTemplate rest;

    public BaseClient(RestTemplate rest) {
        this.rest = rest;
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

    protected ResponseEntity<Object> makeAndSendRequest(HttpMethod method, String path,
                                                        Map<String, Object> parameters, Object body) {
        return makeAndSendRequest(method, path, parameters, body, null);
    }

    protected ResponseEntity<Object> makeAndSendRequest(HttpMethod method, String path,
                                                        Map<String, Object> parameters, Object body, Long userId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (userId != null) {
            headers.set(HeaderConstants.X_SHARER_USER_ID, userId.toString());
        }

        HttpEntity<Object> requestEntity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<Object> shareItServerResponse;
            if (parameters != null) {
                shareItServerResponse = rest.exchange(path, method, requestEntity, Object.class, parameters);
            } else {
                shareItServerResponse = rest.exchange(path, method, requestEntity, Object.class);
            }
            return shareItServerResponse;
        } catch (HttpStatusCodeException ex) {
            String respBody = ex.getResponseBodyAsString();
            Object bodyObj;
            if (respBody == null || respBody.isBlank()) {
                bodyObj = Map.of("error", "Empty response from upstream server");
            } else {
                try {
                    bodyObj = mapper.readValue(respBody, Object.class);
                } catch (Exception parseEx) {
                    bodyObj = respBody;
                }
            }
            return ResponseEntity.status(ex.getStatusCode()).body(bodyObj);
        } catch (ResourceAccessException ex) {
            Map<String, Object> err = Map.of(
                    "status", HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "error", "Internal Server Error",
                    "message", ex.getMessage()
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(err);
        } catch (Exception ex) {
            Map<String, Object> err = Map.of(
                    "status", HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "error", "Internal Server Error",
                    "message", ex.getMessage()
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(err);
        }
    }
}