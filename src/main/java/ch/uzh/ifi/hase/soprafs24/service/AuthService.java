package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Service
public class AuthService {
    @Autowired
    private UserService userService;
    private Map<String, String> tokenCache = new HashMap<>();

    private BCryptPasswordEncoder passwordEncoder;
    int tokenLength = 20;

    public String userLogin(String email, String password) {
        if(userService.authenticateUser(email,password)) {
            String userId = userService.getUser(email).getId();
            String token = generateRandomToken();
            tokenCache.put(token, userId);
            return token;
        }
        else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Wrong username or password.");
        }
    }

    public void addUserToTokenCache(String username, String token){
        tokenCache.put(token, username);
    }
    public String generateRandomToken(){
        byte[] randomBytes = new byte[tokenLength];
        new SecureRandom().nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }

    public boolean isTokenValid(String token){
        if(tokenCache.containsKey(token)){
            return true;
        }
        return false;
    }



    public String getId(String token) {
        if(tokenCache != null && token != null){
            String id = tokenCache.get(token);
            return id;
        }
        return null;
    }

    public void setId(String token, String id){
        if (tokenCache.containsKey(token)){
            tokenCache.replace(token, id);
        }
    }

    public void logout(String token) {
        if(tokenCache != null && token != null){
            tokenCache.remove(token);
        }
    }
}
