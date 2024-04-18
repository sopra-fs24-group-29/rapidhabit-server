package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.entity.ChatMessage;
import ch.uzh.ifi.hase.soprafs24.entity.ChatRoom;
import ch.uzh.ifi.hase.soprafs24.repository.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import ch.uzh.ifi.hase.soprafs24.repository.GroupRepository;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs24.repository.UserScoreRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;


@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final Logger log = LoggerFactory.getLogger(GroupService.class);

    private final GroupRepository groupRepository;
    private final UserScoreRepository userScoreRepository;

    private final UserRepository userRepository;
    private final UserService userService;
    private final BCryptPasswordEncoder encoder;

    private final ChatRoomRepository chatRoomRepository;

    @Autowired
    public ChatRoomService(ChatRoomRepository chatRoomRepository,GroupRepository groupRepository, BCryptPasswordEncoder encoder, UserScoreRepository userScoreRepository, UserRepository userRepository, UserService userService) {
        this.chatRoomRepository = chatRoomRepository;
        this.groupRepository = groupRepository;
        this.encoder = encoder;
        this.userScoreRepository = userScoreRepository;
        this.userRepository = userRepository;
        this.userService = userService;
    }

    public ChatRoom createChatRoom(String groupId) {
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setGroupId(groupId);
        chatRoom.setMessages(new ArrayList<>());
        return chatRoomRepository.save(chatRoom);
    }

    public void sendMessage(String chatRoomId, String senderId, String content) {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setSenderId(senderId);
        chatMessage.setContent(content);
        chatMessage.setTimestamp(LocalDateTime.now());

        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Chat room not found"));
        chatRoom.getMessages().add(chatMessage);
        chatRoomRepository.save(chatRoom);
    }

    public List<ChatMessage> getMessages(String chatRoomId) {
        return chatRoomRepository.findById(chatRoomId)
                .map(ChatRoom::getMessages)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Chat room not found"));
    }
}
