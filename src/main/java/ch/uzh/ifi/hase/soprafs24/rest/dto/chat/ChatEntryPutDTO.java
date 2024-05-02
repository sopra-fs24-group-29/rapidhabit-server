package ch.uzh.ifi.hase.soprafs24.rest.dto.chat;

import java.time.LocalDateTime;

public class ChatEntryPutDTO {
    String token;
    private LocalDateTime date;
    private String message;

    public String getToken() {return token;}

    public void setToken(String token) {
        this.token = token;
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
