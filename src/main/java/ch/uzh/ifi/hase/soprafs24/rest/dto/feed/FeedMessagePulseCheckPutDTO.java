package ch.uzh.ifi.hase.soprafs24.rest.dto.feed;

public class FeedMessagePulseCheckPutDTO {
    String formId;
    String userId;
    Double value;

    public void setFormId(String formId){this.formId = formId;}

    public void setUserId(String userId){
        this.userId = userId;
    }
    public void setValue(Double value){
        this.value = value;
    }

    public String getFormId(){
        return this.formId;
    }
    public String getUserId(){
        return this.userId;
    }
    public Double getValue(){
        return this.value;
    }
}
