package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.entity.ChatMessage;
import ch.uzh.ifi.hase.soprafs24.entity.Group;
import ch.uzh.ifi.hase.soprafs24.rest.dto.chat.ChatEntryPutDTO;
import ch.uzh.ifi.hase.soprafs24.service.AuthService;
import ch.uzh.ifi.hase.soprafs24.service.ChatRoomService;
import ch.uzh.ifi.hase.soprafs24.service.GroupService;
import ch.uzh.ifi.hase.soprafs24.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MessageControllerTest {

    @Mock
    private SimpMessagingTemplate simpMessagingTemplate;

    @Mock
    private AuthService authService;

    @Mock
    private UserService userService;

    @Mock
    private ChatRoomService chatRoomService;

    @Mock
    private GroupService groupService;

    @InjectMocks
    private MessageController messageController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSendChatMessageToGroup_ValidUser() {
        // Arrange
        String groupId = "groupId";
        String userId = "userId";
        ChatEntryPutDTO chatEntryPutDTO = new ChatEntryPutDTO();
        chatEntryPutDTO.setToken("validToken");
        chatEntryPutDTO.setMessage("Hello Group!");

        Group mockGroup = mock(Group.class);
        when(mockGroup.getUserIdList()).thenReturn(Arrays.asList(userId));

        when(authService.isTokenValid(chatEntryPutDTO.getToken())).thenReturn(true);
        when(authService.getId(chatEntryPutDTO.getToken())).thenReturn(userId);
        when(groupService.getGroupById(groupId)).thenReturn(mockGroup);
        when(userService.getInitials(userId)).thenReturn("UI");

        ArgumentCaptor<ChatMessage> chatMessageCaptor = ArgumentCaptor.forClass(ChatMessage.class);

        // Act
        messageController.sendChatMessageToGroup(chatEntryPutDTO, groupId);

        // Assert
        verify(simpMessagingTemplate).convertAndSend(eq("/topic/groups/" + groupId + "/chat"), chatMessageCaptor.capture());
        ChatMessage sentMessage = chatMessageCaptor.getValue();
        assertEquals("Hello Group!", sentMessage.getMessage());
        assertEquals(userId, sentMessage.getUserId());
        assertEquals("UI", sentMessage.getUserInitials());
        assertNotNull(sentMessage.getDate());
        verify(chatRoomService).saveChatMessageToDatabase(groupId, sentMessage);
    }

    @Test
    void testSendChatMessageToGroup_InvalidToken() {
        // Arrange
        String groupId = "groupId";
        ChatEntryPutDTO chatEntryPutDTO = new ChatEntryPutDTO();
        chatEntryPutDTO.setToken("invalidToken");
        chatEntryPutDTO.setMessage("Hello Group!");

        when(authService.isTokenValid(chatEntryPutDTO.getToken())).thenReturn(false);

        // Act
        messageController.sendChatMessageToGroup(chatEntryPutDTO, groupId);

        // Assert
        verify(simpMessagingTemplate).convertAndSend(eq("/topic/groups/" + groupId + "/errors"), eq("Invalid authentication token for user"));
        verify(chatRoomService, never()).saveChatMessageToDatabase(anyString(), any(ChatMessage.class));
    }

    @Test
    void testSendChatMessageToGroup_UserNotInGroup() {
        // Arrange
        String groupId = "groupId";
        String userId = "userId";
        ChatEntryPutDTO chatEntryPutDTO = new ChatEntryPutDTO();
        chatEntryPutDTO.setToken("validToken");
        chatEntryPutDTO.setMessage("Hello Group!");

        Group mockGroup = mock(Group.class);
        when(mockGroup.getUserIdList()).thenReturn(Collections.emptyList());

        when(authService.isTokenValid(chatEntryPutDTO.getToken())).thenReturn(true);
        when(authService.getId(chatEntryPutDTO.getToken())).thenReturn(userId);
        when(groupService.getGroupById(groupId)).thenReturn(mockGroup);

        // Act
        messageController.sendChatMessageToGroup(chatEntryPutDTO, groupId);

        // Assert
        verify(simpMessagingTemplate).convertAndSend(eq("/topic/groups/" + groupId + "/errors"), eq("User is not part of the group"));
        verify(chatRoomService, never()).saveChatMessageToDatabase(anyString(), any(ChatMessage.class));
    }
}
