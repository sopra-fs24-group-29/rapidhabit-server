package ch.uzh.ifi.hase.soprafs24.entity;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatMessage {
    private String senderId;
    private String content;
    private LocalDateTime timestamp;

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getContent() {return content;}

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getTimestamp() {
        return this.timestamp;
    }
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
