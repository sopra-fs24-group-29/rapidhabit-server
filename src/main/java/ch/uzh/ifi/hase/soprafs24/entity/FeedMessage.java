package ch.uzh.ifi.hase.soprafs24.entity;

import ch.uzh.ifi.hase.soprafs24.constant.FeedType;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashMap;

@Document(collection = "FeedMessages")
public class FeedMessage implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private String id;
    private String message;
    @Indexed
    private String groupId;

    @Indexed String formId;
    private String groupName;
    private FeedType type;

    private HashMap<String, Double> userSubmits;
    @Indexed
    private LocalDateTime dateTime;

    public FeedMessage(String formId, String groupId, String groupName, String message, FeedType type, LocalDateTime dateTime) {
        this.formId = formId;
        this.groupId = groupId;
        this.groupName = groupName;
        this.message = message;
        this.type = type;
        this.userSubmits = new HashMap<>();
        this.dateTime = dateTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public HashMap<String,Double> getUserSubmits() {
        return userSubmits;
    }

    public void setUserSubmits(HashMap<String,Double> userSubmits) {
        this.userSubmits = userSubmits;
    }

    public void addUserSubmits(String userId, Double value){
        this.userSubmits.put(userId,value);
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getFormId() {
        return formId;
    }

    public void setFormId(String formId) {
        this.formId = formId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public FeedType getType() {
        return type;
    }

    public void setType(FeedType type) {
        this.type = type;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    @Override
    public String toString() {
        return "FeedMessage{" +
                "id='" + id + '\'' +
                ", message='" + message + '\'' +
                ", groupId='" + groupId + '\'' +
                ", groupName='" + groupName + '\'' +
                ", type=" + type +
                ", userSubmits=" + userSubmits +
                ", dateTime=" + dateTime +
                '}';
    }

}
