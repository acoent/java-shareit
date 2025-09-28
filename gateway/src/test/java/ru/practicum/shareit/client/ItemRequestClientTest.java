package ru.practicum.shareit.client;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import ru.practicum.shareit.dto.ItemRequestDto;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemRequestClientTest {

    @Test
    void createRequest_ShouldCallPostWithCorrectParameters() {
        Long userId = 1L;
        ItemRequestDto requestDto = ItemRequestDto.builder()
                .description("Need a drill")
                .build();
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("created");

        ItemRequestClient spyClient = spy(new ItemRequestClient("http://localhost:8080"));
        doReturn(expectedResponse).when(spyClient).post(eq("/requests"), eq(requestDto), eq(userId));

        ResponseEntity<Object> result = spyClient.createRequest(userId, requestDto);

        assertEquals(expectedResponse, result);
        verify(spyClient).post("/requests", requestDto, userId);
    }

    @Test
    void getUserRequests_ShouldCallGetWithCorrectParameters() {
        Long userId = 1L;
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("requests");

        ItemRequestClient spyClient = spy(new ItemRequestClient("http://localhost:8080"));
        doReturn(expectedResponse).when(spyClient).get(anyString(), any(), eq(userId));

        ResponseEntity<Object> result = spyClient.getUserRequests(userId);

        assertEquals(expectedResponse, result);
        verify(spyClient).get("/requests", null, userId);
    }

    @Test
    void getAllRequests_ShouldCallGetWithCorrectParameters() {
        Long userId = 1L;
        int from = 0;
        int size = 10;
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("all requests");

        ItemRequestClient spyClient = spy(new ItemRequestClient("http://localhost:8080"));
        doReturn(expectedResponse).when(spyClient).get(anyString(), any(Map.class), eq(userId));

        ResponseEntity<Object> result = spyClient.getAllRequests(userId, from, size);

        assertEquals(expectedResponse, result);
        verify(spyClient).get(eq("/requests/all"), argThat(params -> {
            Map<String, Object> paramsMap = (Map<String, Object>) params;
            return paramsMap.get("from").equals(from) && paramsMap.get("size").equals(size);
        }), eq(userId));
    }

    @Test
    void getRequest_ShouldCallGetWithCorrectParameters() {
        Long userId = 1L;
        Long requestId = 2L;
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("request");

        ItemRequestClient spyClient = spy(new ItemRequestClient("http://localhost:8080"));
        doReturn(expectedResponse).when(spyClient).get(anyString(), any(), eq(userId));

        ResponseEntity<Object> result = spyClient.getRequest(userId, requestId);

        assertEquals(expectedResponse, result);
        verify(spyClient).get("/requests/" + requestId, null, userId);
    }

    @Test
    void constructor_ShouldCreateClientWithCorrectServerUrl() {
        String serverUrl = "http://test-server:8080";
        ItemRequestClient client = new ItemRequestClient(serverUrl);

        assertNotNull(client);
    }
}