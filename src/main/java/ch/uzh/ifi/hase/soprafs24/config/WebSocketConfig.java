package ch.uzh.ifi.hase.soprafs24.config;

import ch.uzh.ifi.hase.soprafs24.service.AuthService;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private static final String ORIGIN_LOCALHOST = "http://localhost:3000";
    private static final String ORIGIN_PROD = "ZIEL-URL-CLIENT-GOOGLE";
    private static final String ORIGIN_TEST = "*";

    private final AuthService authService;

    WebSocketConfig(AuthService authService){
        this.authService = authService;
    }
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic"); // enables the broker to send messages to all clients subscribed to "/topics"
        registry.setApplicationDestinationPrefixes("/app"); // implies that user must send messages to "/app/topics"
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry){
        registry.addEndpoint("/ws")
                .setAllowedOrigins(ORIGIN_TEST)
                .setHandshakeHandler(new DefaultHandshakeHandler())
                .addInterceptors(new AuthHandshakeInterceptor(authService));
    }
}
