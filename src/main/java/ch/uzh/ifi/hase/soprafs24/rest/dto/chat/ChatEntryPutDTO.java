package ch.uzh.ifi.hase.soprafs24.rest.dto.chat;

public class ChatEntryPutDTO {
    String token;
    private String message;

    public String getToken() {return token;}

    public void setToken(String token) {
        this.token = token;
    }

    public String getMessage() {return message;}

    public void setMessage(String message) {
        this.message = message;
    }
}
