package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setUp() throws Exception {
        // Make the tokenCache field accessible
        Field tokenCacheField = AuthService.class.getDeclaredField("tokenCache");
        tokenCacheField.setAccessible(true);

        // Initialize the tokenCache field with a new HashMap
        Map<String, String> tokenCache = new HashMap<>();
        tokenCacheField.set(authService, tokenCache);
    }

    private Map<String, String> getTokenCache() throws Exception {
        Field tokenCacheField = AuthService.class.getDeclaredField("tokenCache");
        tokenCacheField.setAccessible(true);
        return (Map<String, String>) tokenCacheField.get(authService);
    }

    @Test
    void userLogin_success() {
        // Given
        String email = "test@example.com";
        String password = "password";
        when(userService.authenticateUser(email, password)).thenReturn(true);
        when(userService.getUser(email)).thenReturn(new User());

        // When
        String token = authService.userLogin(email, password);

        // Then
        assertNotNull(token);
        assertTrue(authService.isTokenValid(token));
    }

    @Test
    void userLogin_failure() {
        // Given
        String email = "test@example.com";
        String password = "wrongPassword";
        when(userService.authenticateUser(email, password)).thenReturn(false);

        // When & Then
        assertThrows(ResponseStatusException.class, () -> authService.userLogin(email, password));
    }

    @Test
    void generateRandomToken() {
        // Given
        int originalLength = 20;
        int expectedLength = originalLength + (int)Math.ceil((double)originalLength / 3.0);

        // When
        String token = authService.generateRandomToken();

        // Then
        assertNotNull(token);
        assertEquals(expectedLength, token.length());
    }


    @Test
    void isTokenValid() throws Exception {
        // Given
        String token = "testToken";
        Map<String, String> tokenCache = getTokenCache();
        tokenCache.put(token, "userId");

        // When
        boolean isValid = authService.isTokenValid(token);

        // Then
        assertTrue(isValid);
    }

    @Test
    void getId() throws Exception {
        // Given
        String token = "testToken";
        String userId = "userId";
        Map<String, String> tokenCache = getTokenCache();
        tokenCache.put(token, userId);

        // When
        String id = authService.getId(token);

        // Then
        assertEquals(userId, id);
    }

    @Test
    void logout() throws Exception {
        // Given
        String token = "testToken";
        Map<String, String> tokenCache = getTokenCache();
        tokenCache.put(token, "userId");

        // When
        authService.logout(token);

        // Then
        assertFalse(authService.isTokenValid(token));
    }
}
