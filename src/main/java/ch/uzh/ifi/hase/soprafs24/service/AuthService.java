package ch.uzh.ifi.hase.soprafs24.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService {

    private final UserService userService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final Map<String, String> tokenCache = new HashMap<>();
    private final int tokenLength = 20;

    @Autowired
    public AuthService(UserService userService, BCryptPasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    public String userLogin(String email, String password) {
        if (userService.authenticateUser(email, password)) {
            String userId = userService.getUser(email).getId();
            String token = generateRandomToken();
            tokenCache.put(token, userId);
            return token;
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Wrong username or password.");
        }
    }

    public String generateRandomToken() {
        byte[] randomBytes = new byte[tokenLength];
        new SecureRandom().nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }

    public boolean isTokenValid(String token) {
        return tokenCache.containsKey(token);
    }

    public String getId(String token) {
        return token != null ? tokenCache.get(token) : null;
    }

    public void logout(String token) {
        if (token != null) {
            tokenCache.remove(token);
        }
    }
}
