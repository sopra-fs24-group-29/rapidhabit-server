package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.entity.ChatMessage;
import ch.uzh.ifi.hase.soprafs24.entity.FeedMessage;
import ch.uzh.ifi.hase.soprafs24.rest.dto.chat.ChatEntryPutDTO;
import ch.uzh.ifi.hase.soprafs24.service.AuthService;
import ch.uzh.ifi.hase.soprafs24.service.ChatRoomService;
import ch.uzh.ifi.hase.soprafs24.service.GroupService;
import ch.uzh.ifi.hase.soprafs24.service.UserService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class MessageController {

    private final SimpMessagingTemplate simpMessagingTemplate;
    private final AuthService authService;
    private final UserService userService;
    private final ChatRoomService chatRoomService;
    private final GroupService groupService;

    public MessageController(SimpMessagingTemplate simpMessagingTemplate, AuthService authService, UserService userService, ChatRoomService chatRoomService, GroupService groupService) {
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.authService = authService;
        this.userService = userService;
        this.chatRoomService = chatRoomService;
        this.groupService = groupService;
    }


    // Dynamisches Mapping für Gruppen
    // Input Mapping: /app/groups/groupId
    // Die Methode mappt eingehende Nachrichten auf Basis der groupId und sendet sie an den entsprechenden Topic.
    @MessageMapping("/groups/{groupId}/feed")
    public void sendFeedMessageToGroup(@Payload FeedMessage feedMessage, @PathVariable("groupId") String groupId) {
        simpMessagingTemplate.convertAndSend("/topic/groups/" + groupId +"/feed", feedMessage);
        System.out.println("MESSAGE: " +feedMessage +" was successfully passed.");
    }

    @MessageMapping("/groups/{groupId}/chat")
    public void sendChatMessageToGroup(@Payload ChatEntryPutDTO chatEntryPutDTO, @PathVariable("groupId") String groupId) {
        try {
            if (authService.isTokenValid(chatEntryPutDTO.getToken())) {
                String userId = authService.getId(chatEntryPutDTO.getToken());
                if(groupService.getGroupById(groupId).getUserIdList().contains(userId)){

                    // DTO Mapping
                    ChatMessage chatMessage = new ChatMessage();
                    chatMessage.setMessage(chatEntryPutDTO.getMessage());
                    chatMessage.setDate(chatEntryPutDTO.getDate());

                    chatMessage.setUserId(userId);
                    chatMessage.setUserInitials(userService.getInitials(userId));
                    simpMessagingTemplate.convertAndSend("/topic/groups/" + groupId + "/chat", chatMessage);
                    chatRoomService.saveChatMessageToDatabase(groupId, chatMessage);
                } else {
                    throw new IllegalArgumentException("User is not part of the group");
                }
            } else {
                throw new IllegalArgumentException("Invalid authentication token for user");
            }
        } catch (Exception e) {
            simpMessagingTemplate.convertAndSend("/topic/groups/" + groupId + "/errors", e.getMessage());
            System.out.println("Error: " + e.getMessage());
        }
    }


    public void sendFeedToGroup(String groupId, FeedMessage feedMessage) {
        simpMessagingTemplate.convertAndSend("/topic/groups/" + groupId + "/feed", feedMessage);
        System.out.println("Notification sent to group " + groupId + ": " + feedMessage);
    }
}
