package ch.uzh.ifi.hase.soprafs24.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import ch.uzh.ifi.hase.soprafs24.service.AuthService;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private static final String ORIGIN_LOCALHOST = "http://localhost:3000";
    private static final String ORIGIN_PROD = "https://sopra-fs24-group29-client.oa.r.appspot.com";

    private final AuthService authService;

    public WebSocketConfig(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic"); // setting up the destinations
        registry.setApplicationDestinationPrefixes("/app"); // every message sent to this prefix ends up in controller X
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Endpoint without using SockJS
        registry.addEndpoint("/ws")
                .setAllowedOrigins(ORIGIN_LOCALHOST, ORIGIN_PROD)
                .setHandshakeHandler(new DefaultHandshakeHandler());
                // .addInterceptors(new AuthHandshakeInterceptor(authService));

        // Endpoint using SockJS
        registry.addEndpoint("/ws-sockjs")
                .setAllowedOrigins("*")
                .setHandshakeHandler(new DefaultHandshakeHandler());
                //.addInterceptors(new AuthHandshakeInterceptor(authService))
                //.withSockJS();
    }

}
