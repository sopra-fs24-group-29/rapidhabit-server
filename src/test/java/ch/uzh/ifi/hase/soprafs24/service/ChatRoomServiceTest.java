package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.entity.ChatMessage;
import ch.uzh.ifi.hase.soprafs24.entity.ChatRoom;
import ch.uzh.ifi.hase.soprafs24.repository.ChatRoomRepository;
import ch.uzh.ifi.hase.soprafs24.repository.GroupRepository;
import ch.uzh.ifi.hase.soprafs24.repository.UserScoreRepository;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChatRoomServiceTest {

    @Mock
    private ChatRoomRepository chatRoomRepository;

    @Mock
    private GroupRepository groupRepository;

    @Mock
    private UserScoreRepository userScoreRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserService userService;

    @Mock
    private BCryptPasswordEncoder encoder;

    @InjectMocks
    private ChatRoomService chatRoomService;

    @BeforeEach
    public void setUp() {
        // Initialize mocks here if necessary
    }

    @Test
    void testCreateChatRoom() {
        // Arrange
        String groupId = "123";
        ChatRoom expectedChatRoom = new ChatRoom();
        expectedChatRoom.setGroupId(groupId);
        expectedChatRoom.setMessages(new ArrayList<>());

        when(chatRoomRepository.save(any(ChatRoom.class))).thenReturn(expectedChatRoom);

        // Act
        ChatRoom actualChatRoom = chatRoomService.createChatRoom(groupId);

        // Assert
        assertEquals(expectedChatRoom, actualChatRoom);
        verify(chatRoomRepository, times(1)).save(any(ChatRoom.class));
    }
    @Test
    void testGetChatMessages() {
        // Arrange
        String groupId = "123";
        List<ChatMessage> messages = new ArrayList<>();
        messages.add(new ChatMessage());
        messages.add(new ChatMessage());
        messages.add(new ChatMessage());

        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setGroupId(groupId);
        chatRoom.setMessages(messages);

        when(chatRoomRepository.findByGroupId(groupId)).thenReturn(Optional.of(chatRoom));

        // Act
        List<ChatMessage> actualMessages = chatRoomService.getChatMessages(groupId, 2);

        // Assert
        assertEquals(2, actualMessages.size());
        assertEquals(messages.get(1), actualMessages.get(0));
        assertEquals(messages.get(2), actualMessages.get(1));
        verify(chatRoomRepository, times(1)).findByGroupId(groupId);
    }
    @Test
    void testSaveChatMessageToDatabase() {
        // Arrange
        String groupId = "123";
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setUserId("userId");

        // Create a ChatRoom object with an initialized messages list
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setGroupId(groupId);
        chatRoom.setMessages(new ArrayList<>()); // Initialize the messages list

        // When findByGroupId is called with groupId, return the ChatRoom object
        when(chatRoomRepository.findByGroupId(groupId)).thenReturn(Optional.of(chatRoom));

        // Act
        chatRoomService.saveChatMessageToDatabase(groupId, chatMessage);

        // Assert
        verify(chatRoomRepository, times(1)).findByGroupId(groupId);
        verify(chatRoomRepository, times(1)).save(any(ChatRoom.class));
    }

    @Test
    void testDeleteChatRoom() {
        // Arrange
        String groupId = "123";

        when(chatRoomRepository.findByGroupId(groupId)).thenReturn(Optional.of(new ChatRoom()));

        // Act
        chatRoomService.deleteChatRoom(groupId);

        // Assert
        verify(chatRoomRepository, times(1)).findByGroupId(groupId);
        verify(chatRoomRepository, times(1)).delete(any(ChatRoom.class));
    }
    @Test
    void testSendMessage() {
        // Arrange
        String chatRoomId = "123";
        String userId = "userId";
        String message = "Hello, world";

        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setUserId(userId);
        chatMessage.setMessage(message);
        chatMessage.setDate(LocalDateTime.now());

        // Create a ChatRoom object with an initialized messages list
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setId(chatRoomId); // Assuming ChatRoom has an ID field
        chatRoom.setMessages(new ArrayList<>()); // Initialize the messages list

        // When findById is called with chatRoomId, return the ChatRoom object
        when(chatRoomRepository.findById(chatRoomId)).thenReturn(Optional.of(chatRoom));

        // When save is called with any ChatRoom, return the ChatRoom object
        when(chatRoomRepository.save(any(ChatRoom.class))).thenReturn(chatRoom);

        // Act
        chatRoomService.sendMessage(chatRoomId, userId, message);

        // Assert
        verify(chatRoomRepository, times(1)).findById(chatRoomId);
        verify(chatRoomRepository, times(1)).save(any(ChatRoom.class));
    }

    @Test
    void testGetMessages() {
        // Arrange
        String chatRoomId = "123";

        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setMessages(Arrays.asList(new ChatMessage(), new ChatMessage()));

        when(chatRoomRepository.findById(chatRoomId)).thenReturn(Optional.of(chatRoom));

        // Act
        List<ChatMessage> actualMessages = chatRoomService.getMessages(chatRoomId);

        // Assert
        assertEquals(2, actualMessages.size());
        verify(chatRoomRepository, times(1)).findById(chatRoomId);
    }
}
