package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.constant.UserStatsStatus;
import ch.uzh.ifi.hase.soprafs24.constant.Weekday;
import ch.uzh.ifi.hase.soprafs24.entity.*;
import ch.uzh.ifi.hase.soprafs24.repository.GroupRepository;
import ch.uzh.ifi.hase.soprafs24.rest.dto.habit.HabitGetDTO;
import ch.uzh.ifi.hase.soprafs24.service.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.web.server.ResponseStatusException;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.util.*;

@WebMvcTest(ChatRoomController.class)
class ChatRoomControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @MockBean
    private GroupService groupService;

    @MockBean
    private ChatRoomService chatRoomService;

    @MockBean
    private UserService userService;

    @MockBean
    private UserStatsEntryService userStatsEntryService;

    @MockBean
    private HabitService habitService;

    @MockBean
    private UserScoreService userScoreService;

    @MockBean
    private GroupRepository groupRepository;

    @Test //GET Mapping "/groups/{groupId}/chat" - CODE 200 OK (Pass)
    void testGetChatMessages_ValidToken_UserInGroup() throws Exception {
        // Arrange
        String authToken = "JaZAJ6m4_wh7_ClFK5jr6vvnyRA";
        String groupId = "123";
        String userId = "validUserId";
        List<ChatMessage> chatMessages = new ArrayList<>();

        when(authService.isTokenValid(authToken)).thenReturn(true);
        when(authService.getId(authToken)).thenReturn(userId);

        Group group = new Group();
        group.setUserIdList(Arrays.asList(userId));
        when(groupService.getGroupById(groupId)).thenReturn(group);

        when(chatRoomService.getChatMessages(groupId, 20)).thenReturn(chatMessages);

        mockMvc.perform(get("/groups/{groupId}/chat", groupId)
                        .header("Authorization", authToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test //GET Mapping "/groups/{groupId}/chat" - CODE 401 Unauthorized (Error)
    void testGetChatMessages_InvalidToken_UserInGroup() throws Exception {
        // Arrange
        String authToken = "JaZAJ6m4_wh7_ClFK5jr6vvnyRA";
        String groupId = "123";
        String userId = "validUserId";
        List<ChatMessage> chatMessages = new ArrayList<>();

        when(authService.isTokenValid(authToken)).thenReturn(false);

        mockMvc.perform(get("/groups/{groupId}/chat", groupId)
                        .header("Authorization", authToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test //GET Mapping "/groups/{groupId}/chat" - CODE 401 Unauthorized (Error)
    void testGetChatMessages_ValidToken_UserNotInGroup() throws Exception {
        // Arrange
        String authToken = "JaZAJ6m4_wh7_ClFK5jr6vvnyRA";
        String groupId = "123";
        String userId = "validUserId";
        List<ChatMessage> chatMessages = new ArrayList<>();

        when(authService.isTokenValid(authToken)).thenReturn(true);
        when(authService.getId(authToken)).thenReturn(userId);

        Group group = new Group();
        group.setUserIdList(Collections.emptyList());
        when(groupService.getGroupById(groupId)).thenReturn(group);

        when(chatRoomService.getChatMessages(groupId, 20)).thenReturn(chatMessages);

        mockMvc.perform(get("/groups/{groupId}/chat", groupId)
                        .header("Authorization", authToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

}
