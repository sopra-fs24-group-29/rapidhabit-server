package ch.uzh.ifi.hase.soprafs24.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private static final String ORIGIN_LOCALHOST = "http://localhost:3000";
    private static final String ORIGIN_PROD = "ZIEL-URL-CLIENT-GOOGLE";
    private static final String ORIGIN_TEST = "*";

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/groups/{groupId}/chat");
        registry.setApplicationDestinationPrefixes("/app");
    //            .setHeartbeatValue(new long[] {1000, 1000})
    //            .setTaskScheduler(heartBeatScheduler())
    //            .setHeartbeatValue(new long[] {60000, 120000});
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOrigins(ORIGIN_LOCALHOST, ORIGIN_PROD, ORIGIN_TEST)
                .setHandshakeHandler(new DefaultHandshakeHandler())
                .addInterceptors(new HttpSessionHandshakeInterceptor());

        registry.addEndpoint("/wss")
                .setAllowedOrigins(ORIGIN_LOCALHOST, ORIGIN_PROD, ORIGIN_TEST)
                .setHandshakeHandler(new DefaultHandshakeHandler())
                .addInterceptors(new HttpSessionHandshakeInterceptor());
    }

    @Bean
    public ServletServerContainerFactoryBean createWebSocketContainer() {
        ServletServerContainerFactoryBean container = new ServletServerContainerFactoryBean();
        container.setMaxSessionIdleTimeout(-1L);
        return container;
    }

    @Bean
    public TaskScheduler heartBeatScheduler() {
        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.setPoolSize(1);
        return taskScheduler;
    }
}
