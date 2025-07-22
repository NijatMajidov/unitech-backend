package org.unitech.msaccount;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestTemplate;
import org.unitech.msaccount.client.UserClient;
import org.unitech.msaccount.model.dto.response.UserResponse;
import org.unitech.msaccount.service.UserService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserClient userClient;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getUserById_shouldReturnUserResponse() {
        Long userId = 1L;
        UserResponse response = new UserResponse();
        response.setId(userId);
        response.setEmail("test@example.com");

        when(userClient.getUserById(userId)).thenReturn(response);

        UserResponse actual = userService.getUserById(userId);

        assertNotNull(actual);
        assertEquals(userId, actual.getId());
        assertEquals("test@example.com", actual.getEmail());
    }

    @Test
    void getUserById_shouldThrowWhenUserIsNull() {
        Long userId = 999L;
        when(userClient.getUserById(userId)).thenReturn(null);

        assertThrows(RuntimeException.class, () -> userService.getUserById(userId));
    }
}


