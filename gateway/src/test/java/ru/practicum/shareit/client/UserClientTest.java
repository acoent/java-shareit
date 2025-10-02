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
import ru.practicum.shareit.dto.UserDto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class UserClientTest {

    private RestTemplate restTemplate;
    private UserClient userClient;

    @Captor
    private ArgumentCaptor<HttpEntity> httpEntityCaptor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        restTemplate = mock(RestTemplate.class);
        userClient = new UserClient(restTemplate);
        httpEntityCaptor = ArgumentCaptor.forClass(HttpEntity.class);
    }

    @Test
    void createUser_shouldCallPostWithoutUserHeader() {
        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(ResponseEntity.ok().build());

        UserDto dto = UserDto.builder().name("u").email("u@example.com").build();
        userClient.createUser(dto);

        verify(restTemplate).exchange(eq("/users"), eq(HttpMethod.POST), httpEntityCaptor.capture(), eq(Object.class));
        assertThat(httpEntityCaptor.getValue().getHeaders().containsKey(HeaderConstants.X_SHARER_USER_ID)).isFalse();
    }

    @Test
    void updateUser_shouldCallPatchWithCorrectPath() {
        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(ResponseEntity.ok().build());

        UserDto dto = UserDto.builder().name("u").email("u@example.com").build();
        userClient.updateUser(9L, dto);

        verify(restTemplate).exchange(eq("/users/9"), eq(HttpMethod.PATCH), httpEntityCaptor.capture(), eq(Object.class));
        assertThat(httpEntityCaptor.getValue().getHeaders().getFirst(HeaderConstants.X_SHARER_USER_ID)).isEqualTo("9");
    }

    @Test
    void getUser_shouldCallGetWithCorrectPath() {
        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(ResponseEntity.ok().build());

        userClient.getUser(5L);

        verify(restTemplate).exchange(eq("/users/5"), eq(HttpMethod.GET), any(HttpEntity.class), eq(Object.class));
    }

    @Test
    void getAllUsers_shouldCallGetWithCorrectPath() {
        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(ResponseEntity.ok().build());

        userClient.getAllUsers();

        verify(restTemplate).exchange(eq("/users"), eq(HttpMethod.GET), httpEntityCaptor.capture(), eq(Object.class));
    }

    @Test
    void deleteUser_shouldCallDeleteWithCorrectPath() {
        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), eq(Object.class)))
                .thenReturn(ResponseEntity.ok().build());

        userClient.deleteUser(3L);

        verify(restTemplate).exchange(eq("/users/3"), eq(HttpMethod.DELETE), any(HttpEntity.class), eq(Object.class));
    }

    @Test
    void constructor_shouldCreateClientSuccessfully() {
        UserClient client = new UserClient(restTemplate);
        assertNotNull(client);
    }
}