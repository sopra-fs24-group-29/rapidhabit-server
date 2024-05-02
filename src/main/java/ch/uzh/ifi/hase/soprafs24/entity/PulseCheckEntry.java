package ch.uzh.ifi.hase.soprafs24.entity;

import java.time.LocalDateTime;

import ch.uzh.ifi.hase.soprafs24.constant.PulseCheckStatus;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "PulseCheckEntries")
public class PulseCheckEntry {
    @Id
    private String id;
    @Indexed
    private String formId;
    @Indexed
    private String groupId;
    private String userId;

    private String content;
    private double value;
    @Indexed
    private LocalDateTime creationTimestamp;
    private LocalDateTime submissionTimestamp;
    private PulseCheckStatus status;

    // Constructor already provided
    public PulseCheckEntry(String formId, String groupId, String userId, String content, LocalDateTime creationTimestamp, LocalDateTime submissionTimestamp, PulseCheckStatus status) {
        this.formId = formId;
        this.groupId = groupId;
        this.userId = userId;
        this.content = content;
        this.value = 0;
        this.creationTimestamp = creationTimestamp;
        this.submissionTimestamp = submissionTimestamp;
        this.status = status;
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getFormId() {
        return formId;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getUserId() {
        return userId;
    }

    public double getValue() {
        return value;
    }

    public String getContent() {
        return content;
    }

    public LocalDateTime getCreationTimestamp() {
        return creationTimestamp;
    }

    public LocalDateTime getSubmissionTimestamp() {
        return submissionTimestamp;
    }

    public PulseCheckStatus getStatus() {
        return status;
    }

    // Setters
    public void setId(String id) {
        this.id = id;
    }

    public void setFormId(String formId) {
        this.formId = formId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setCreationTimestamp(LocalDateTime creationTimestamp) {
        this.creationTimestamp = creationTimestamp;
    }

    public void setSubmissionTimestamp(LocalDateTime submissionTimestamp) {
        this.submissionTimestamp = submissionTimestamp;
    }

    public void setStatus(PulseCheckStatus status) {
        this.status = status;
    }
}
