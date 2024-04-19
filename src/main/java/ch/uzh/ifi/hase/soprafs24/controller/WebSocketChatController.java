package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.entity.ChatMessage;
import ch.uzh.ifi.hase.soprafs24.entity.Group;
import ch.uzh.ifi.hase.soprafs24.service.AuthService;
import ch.uzh.ifi.hase.soprafs24.service.ChatRoomService;
import ch.uzh.ifi.hase.soprafs24.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.messaging.Message;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class WebSocketChatController {

    @Autowired
    private ChatRoomService chatRoomService;

    @Autowired
    private AuthService authService;

    @Autowired
    private GroupService groupService;
    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public WebSocketChatController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }



    @MessageMapping("/groups/{groupId}/chat")
    @SendTo("/groups/{groupId}/chat")
    public ChatMessage sendMessageToGroup(@RequestHeader("Authorization") String authHeaderToken, @DestinationVariable String groupId, @Payload ChatMessage chatMessage) {
        String userId = authService.getId(authHeaderToken);
        Group group = groupService.getGroupById(groupId);

        if (!group.getUserIdList().contains(userId)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User is not part of this group");
        }
        chatRoomService.sendMessage(groupId, chatMessage.getSenderId(), chatMessage.getContent());
        return chatMessage;
    }
}
