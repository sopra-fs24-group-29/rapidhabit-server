package ch.uzh.ifi.hase.soprafs24.config;

import ch.uzh.ifi.hase.soprafs24.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

public class AuthHandshakeInterceptor implements HandshakeInterceptor {
    private final AuthService authService;

    @Autowired
    public AuthHandshakeInterceptor(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        String connectionHeader = request.getHeaders().getFirst("Connection");
        String upgradeHeader = request.getHeaders().getFirst("Upgrade");
        String authToken = request.getHeaders().getFirst("Authorization");

        System.out.println("Connection Header: " + connectionHeader);
        System.out.println("Upgrade Header: " + upgradeHeader);
        System.out.println("Authorization Header: " + authToken);

        if (connectionHeader == null || !connectionHeader.equalsIgnoreCase("Upgrade")) {
            System.out.println("Invalid or missing Connection header");
            response.setStatusCode(HttpStatus.BAD_REQUEST);
            return false;
        }

        if (upgradeHeader == null || !upgradeHeader.equalsIgnoreCase("websocket")) {
            System.out.println("Invalid or missing Upgrade header");
            response.setStatusCode(HttpStatus.BAD_REQUEST);
            return false;
        }

        if (authToken == null) {
            System.out.println("Authorization header is missing");
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return false;
        }

        if (authService.isTokenValid(authToken)) {
            System.out.println("Handshake accepted!");
            return true;
        }

        System.out.println("Handshake rejected!");
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        return false;
    }


    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {
    }
}
