package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.service.ChannelService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;
import ch.uzh.ifi.hase.soprafs24.websocket.SubscriptionRequest;

@Controller
public class WebSocketController {

    private final ChannelService channelService;

    public WebSocketController(ChannelService channelService) {
        this.channelService = channelService;
    }

    @MessageMapping("/subscribe")
    @SendTo("/topic/messages")
    public String processMessageFromClient(@Payload String messageBody) throws Exception {
        // Verarbeitungslogik hier...
        return "Danke, Ihre Nachricht wurde verarbeitet: " + messageBody;
    }

    // Handles all messages sent to "/app/echo"
    @MessageMapping("/echo")
    @SendToUser("/queue/reply")
    public String handleEchoMessage(String message) {
        // Einfach die empfangene Nachricht zurückschicken.
        return message;
    }
}
