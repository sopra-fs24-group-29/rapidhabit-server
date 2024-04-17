package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.entity.ChatMessage;
import ch.uzh.ifi.hase.soprafs24.entity.Group;
import ch.uzh.ifi.hase.soprafs24.service.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.messaging.Message;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ch.uzh.ifi.hase.soprafs24.rest.dto.chat.MessageDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/chatRooms")
public class ChatRoomController {

    private final HabitService habitService;

    private final GroupService groupService;

    private final UserService userService;
    private final ObjectMapper objectMapper;

    private final AuthService authService;

    private final UserStatsEntryService userStatsEntryService;

    private final HabitStreakService habitStreakService;

    private final ChatRoomService chatRoomService;

    @Autowired
    public ChatRoomController(ChatRoomService chatRoomService, HabitService habitService, GroupService groupService, UserService userService, AuthService authService, ObjectMapper objectMapper, UserStatsEntryService userStatsEntryService, HabitStreakService habitStreakService) {
        this.chatRoomService = chatRoomService;
        this.habitService = habitService;
        this.objectMapper = objectMapper;
        this.groupService = groupService;
        this.userService = userService;
        this.authService = authService;
        this.userStatsEntryService = userStatsEntryService;
        this.habitStreakService = habitStreakService;
    }

    @PostMapping("/{chatRoomId}/messages")
    public ResponseEntity<?> sendMessage(@RequestHeader("Authorization") String authHeaderToken, @PathVariable String chatRoomId, String groupId, @RequestBody MessageDTO messageDTO) {
        boolean isValid = authService.isTokenValid(authHeaderToken);
        if (!isValid) {
            return new ResponseEntity<>("Invalid token", HttpStatus.UNAUTHORIZED);
        }
        String userId = authService.getId(authHeaderToken);
        Group group = groupService.getGroupById(groupId);

        if (!group.getUserIdList().contains(userId)) {
            return new ResponseEntity<>("User is not part of this group", HttpStatus.UNAUTHORIZED);
        }

        chatRoomService.sendMessage(chatRoomId, messageDTO.getSenderId(), messageDTO.getContent());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{chatRoomId}/messages")
    public ResponseEntity<List<ChatMessage>> getMessages(@RequestHeader("Authorization") String authHeaderToken, @PathVariable String chatRoomId) {
        return ResponseEntity.ok(chatRoomService.getMessages(chatRoomId));
    }
}
