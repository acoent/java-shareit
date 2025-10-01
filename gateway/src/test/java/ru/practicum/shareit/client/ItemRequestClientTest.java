package ru.practicum.shareit.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import ru.practicum.shareit.common.HeaderConstants;
import ru.practicum.shareit.dto.ItemRequestDto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ItemRequestClientTest {

    private RestTemplate restTemplate;
    private ItemRequestClient itemRequestClient;

    @Captor
    private ArgumentCaptor<HttpEntity> httpEntityCaptor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        restTemplate = mock(RestTemplate.class);
        itemRequestClient = new ItemRequestClient(restTemplate);
        httpEntityCaptor = ArgumentCaptor.forClass(HttpEntity.class);
    }

    @Test
    void createRequest_shouldCallPostWithCorrectParameters() {
        ItemRequestDto requestDto = ItemRequestDto.builder()
                .description("Need a drill")
                .build();
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("created");

        ItemRequestClient spyClient = spy(itemRequestClient);
        doReturn(expectedResponse).when(spyClient).post(eq("/requests"), eq(requestDto), eq(1L));

        ResponseEntity<Object> result = spyClient.createRequest(1L, requestDto);

        assertEquals(expectedResponse, result);
        verify(spyClient).post("/requests", requestDto, 1L);
    }

    @Test
    void createRequest_shouldSetCorrectHeaders() {
        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(ResponseEntity.ok().build());

        ItemRequestDto dto = ItemRequestDto.builder().description("need it").build();
        itemRequestClient.createRequest(7L, dto);

        verify(restTemplate).exchange(eq("/requests"), eq(HttpMethod.POST), httpEntityCaptor.capture(), eq(Object.class));
        assertThat(httpEntityCaptor.getValue().getHeaders().getFirst(HeaderConstants.X_SHARER_USER_ID)).isEqualTo("7");
    }

    @Test
    void getUserRequests_shouldCallGetWithCorrectParameters() {
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("requests");

        ItemRequestClient spyClient = spy(itemRequestClient);
        doReturn(expectedResponse).when(spyClient).get(anyString(), any(), eq(1L));

        ResponseEntity<Object> result = spyClient.getUserRequests(1L);

        assertEquals(expectedResponse, result);
        verify(spyClient).get("/requests", null, 1L);
    }

    @Test
    void getAllRequests_shouldCallGetWithCorrectParameters() {
        int from = 0;
        int size = 10;
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("all requests");

        ItemRequestClient spyClient = spy(itemRequestClient);
        doReturn(expectedResponse).when(spyClient).get(anyString(), any(), eq(1L));

        ResponseEntity<Object> result = spyClient.getAllRequests(1L, from, size);

        assertEquals(expectedResponse, result);
        verify(spyClient).get(contains("/requests/all"), isNull(), eq(1L));
        verify(spyClient).get(contains("from=0"), isNull(), eq(1L));
        verify(spyClient).get(contains("size=10"), isNull(), eq(1L));
    }

    @Test
    void getAllRequests_shouldBuildQueryCorrectly() {
        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(ResponseEntity.ok().build());

        itemRequestClient.getAllRequests(5L, 1, 20);

        verify(restTemplate).exchange(contains("/requests/all"), eq(HttpMethod.GET), any(HttpEntity.class), eq(Object.class));
        verify(restTemplate).exchange(contains("from=1"), eq(HttpMethod.GET), any(HttpEntity.class), eq(Object.class));
        verify(restTemplate).exchange(contains("size=20"), eq(HttpMethod.GET), any(HttpEntity.class), eq(Object.class));
    }

    @Test
    void getAllRequests_shouldSetCorrectHeaders() {
        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(ResponseEntity.ok().build());

        itemRequestClient.getAllRequests(8L, 0, 10);

        verify(restTemplate).exchange(contains("/requests/all"), eq(HttpMethod.GET), httpEntityCaptor.capture(), eq(Object.class));
    }

    @Test
    void getRequest_shouldCallGetWithCorrectParameters() {
        Long requestId = 2L;
        ResponseEntity<Object> expectedResponse = ResponseEntity.ok("request");

        ItemRequestClient spyClient = spy(itemRequestClient);
        doReturn(expectedResponse).when(spyClient).get(anyString(), any(), eq(1L));

        ResponseEntity<Object> result = spyClient.getRequest(1L, requestId);

        assertEquals(expectedResponse, result);
        verify(spyClient).get(contains("/requests/2"), isNull(), eq(1L));
    }

    @Test
    void constructor_shouldCreateClientSuccessfully() {
        ItemRequestClient client = new ItemRequestClient(restTemplate);
        assertNotNull(client);
    }
}