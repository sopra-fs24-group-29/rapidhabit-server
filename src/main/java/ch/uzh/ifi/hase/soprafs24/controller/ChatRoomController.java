package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.entity.ChatMessage;
import ch.uzh.ifi.hase.soprafs24.rest.dto.group.GroupGetDTO;
import ch.uzh.ifi.hase.soprafs24.service.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ChatRoomController {
    GroupService groupService;
    AuthService authService;
    UserService userService;

    ChatRoomService chatRoomService;

    ChatRoomController(GroupService groupService, AuthService authService, UserService userService, ChatRoomService chatRoomService) {
        this.groupService = groupService;
        this.authService = authService;
        this.userService = userService;
        this.chatRoomService = chatRoomService;
    }

    @GetMapping("/groups/{groupId}/chat")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public ResponseEntity<?> getChatMessages(@RequestHeader("Authorization") String authToken, @PathVariable String groupId) { // retrieves the last n = 20 chat messages
        boolean isValid = authService.isTokenValid(authToken);
        if(isValid){
            String userId = authService.getId(authToken);
            if(groupService.getGroupById(groupId).getUserIdList().contains(userId)){ // check if user is part of the group
                // fetch userId of the person who did the request
                int n = 20; // number of messages retrieved
                List<ChatMessage> chatMessages = chatRoomService.getChatMessages(groupId, n);
                return ResponseEntity.ok(chatMessages);
            }
            else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
        }
        else{
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}
