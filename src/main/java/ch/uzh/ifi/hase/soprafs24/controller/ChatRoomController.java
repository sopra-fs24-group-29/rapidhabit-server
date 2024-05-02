package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.entity.ChatMessage;
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
    public ResponseEntity<?> getChatMessages(@RequestHeader("Authorization") String authToken, @PathVariable String groupId) {
        if (!authService.isTokenValid(authToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid or expired authorization token.");
        }

        String userId = authService.getId(authToken);
        if (!groupService.getGroupById(groupId).getUserIdList().contains(userId)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("User is not part of the group.");
        }

        int n = 20; // number of messages retrieved
        List<ChatMessage> chatMessages = chatRoomService.getChatMessages(groupId, n);
        return ResponseEntity.ok(chatMessages);
    }

}
