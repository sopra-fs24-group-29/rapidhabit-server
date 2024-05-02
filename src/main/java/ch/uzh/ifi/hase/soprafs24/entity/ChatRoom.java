package ch.uzh.ifi.hase.soprafs24.entity;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Builder;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.messaging.Message;

import javax.persistence.Id;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "ChatRooms")
public class ChatRoom {
    @Id
    private String id;
    private String groupId;
    private List<ChatMessage> messages;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public List<ChatMessage> getMessages() {
        return this.messages;
    }

    public void setMessages(List<ChatMessage> messages) {
        this.messages = messages;
    }

    public void addMessage(ChatMessage chatMessage){
        messages.add(chatMessage);
    }

}
