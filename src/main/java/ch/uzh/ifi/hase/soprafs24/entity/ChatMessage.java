package ch.uzh.ifi.hase.soprafs24.entity;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatMessage {
    private String userId;
    private String userInitials;
    private LocalDateTime date;
    private String message;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String senderId) {
        this.userId = userId;
    }
    public String getUserInitials() {
        return userInitials;
    }

    public void setUserInitials(String userInitials) {
        this.userInitials = userInitials;
    }

    public String getMessage() {return message;}

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getDate() {
        return this.date;
    }
    public void setDate(LocalDateTime date) {
        this.date = date;
    }
}
