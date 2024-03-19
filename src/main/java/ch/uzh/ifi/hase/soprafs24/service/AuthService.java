package ch.uzh.ifi.hase.soprafs24.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService {
    @Autowired
    private UserService userService;
    private Map<String, String> tokenCache = new HashMap<>();
    int tokenLength = 20;

    public String userLogin(String username, String password){
        if(userService.isUserExists(username, password)){
            String token = generateRandomToken();
            tokenCache.put(token, username);
            return token;
        }
        return null;
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



    public String getUsername(String token) {
        if(tokenCache != null && token != null){
            String userName = tokenCache.get(token);
            return userName;
        }
        return null;
    }

    public void setUsername(String token, String newUsername){
        if (tokenCache.containsKey(token)){
            tokenCache.replace(token, newUsername);
        }
    }

    public void logout(String token) {
        if(tokenCache != null && token != null){
            tokenCache.remove(token);
        }
    }
}
