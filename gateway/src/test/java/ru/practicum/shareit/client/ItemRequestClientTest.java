package ru.practicum.shareit.client;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.dto.ItemRequestDto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemRequestClientTest {

    @Test
    void createRequest_ShouldCallPostWithCorrectParameters() {
        RestTemplate rest = mock(RestTemplate.class);
        ItemRequestDto requestDto = ItemRequestDto.builder()
                .description("Need a drill")
                .build();
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("created");

        ItemRequestClient spyClient = spy(new ItemRequestClient(rest));
        doReturn(expectedResponse).when(spyClient).post(eq("/requests"), eq(requestDto), eq(1L));

        ResponseEntity<Object> result = spyClient.createRequest(1L, requestDto);

        assertEquals(expectedResponse, result);
        verify(spyClient).post("/requests", requestDto, 1L);
    }

    @Test
    void getUserRequests_ShouldCallGetWithCorrectParameters() {
        RestTemplate rest = mock(RestTemplate.class);
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("requests");

        ItemRequestClient spyClient = spy(new ItemRequestClient(rest));
        doReturn(expectedResponse).when(spyClient).get(anyString(), any(), eq(1L));

        ResponseEntity<Object> result = spyClient.getUserRequests(1L);

        assertEquals(expectedResponse, result);
        verify(spyClient).get("/requests", null, 1L);
    }

    @Test
    void getAllRequests_ShouldCallGetWithCorrectParameters() {
        RestTemplate rest = mock(RestTemplate.class);
        int from = 0;
        int size = 10;
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("all requests");

        ItemRequestClient spyClient = spy(new ItemRequestClient(rest));
        doReturn(expectedResponse).when(spyClient).get(anyString(), any(), eq(1L));

        ResponseEntity<Object> result = spyClient.getAllRequests(1L, from, size);

        assertEquals(expectedResponse, result);
        verify(spyClient).get(eq("/requests/all?from=" + from + "&size=" + size), isNull(), eq(1L));
    }

    @Test
    void getRequest_ShouldCallGetWithCorrectParameters() {
        RestTemplate rest = mock(RestTemplate.class);
        Long requestId = 2L;
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("request");

        ItemRequestClient spyClient = spy(new ItemRequestClient(rest));
        doReturn(expectedResponse).when(spyClient).get(anyString(), any(), eq(1L));

        ResponseEntity<Object> result = spyClient.getRequest(1L, requestId);

        assertEquals(expectedResponse, result);
        verify(spyClient).get("/requests/" + requestId, null, 1L);
    }

    @Test
    void constructor_ShouldCreateClientWithCorrectServerUrl() {
        RestTemplate rest = mock(RestTemplate.class);
        String serverUrl = "http://test-server:8080";
        ItemRequestClient client = new ItemRequestClient(rest);

        assertNotNull(client);
    }
}
