package ch.uzh.ifi.hase.soprafs24.rest.dto.feed;

public class FeedMessagePulseCheckPutDTO {
    String userId;
    Double value;

    public void setUserId(String userId){
        this.userId = userId;
    }
    public void setValue(Double value){
        this.value = value;
    }

    public String getUserId(){
        return this.userId;
    }
    public Double getValue(){
        return this.value;
    }
}
