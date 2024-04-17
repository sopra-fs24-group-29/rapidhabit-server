package ch.uzh.ifi.hase.soprafs24.rest.dto.chat;

import lombok.*;

import java.time.LocalDateTime;

public class MessageDTO {

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

}
