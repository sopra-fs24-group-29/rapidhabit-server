package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.entity.FeedMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class MessageController {

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    // Dynamisches Mapping für Gruppen
    // Input Mapping: /app/groups/groupId
    // Die Methode mappt eingehende Nachrichten auf Basis der groupId und sendet sie an den entsprechenden Topic.
    @MessageMapping("/groups/{groupId}/feed")
    public void sendFeedMessageToGroup(@Payload FeedMessage feedMessage, @PathVariable("groupId") String groupId) {
        simpMessagingTemplate.convertAndSend("/topic/groups/" + groupId +"/feed", feedMessage);
        System.out.println("MESSAGE: " +feedMessage +" was successfully passed.");
    }

    public void sendFeedToGroup(String groupId, FeedMessage feedMessage) {
        simpMessagingTemplate.convertAndSend("/topic/groups/" + groupId + "/feed", feedMessage);
        System.out.println("Notification sent to group " + groupId + ": " + feedMessage);
    }
}
