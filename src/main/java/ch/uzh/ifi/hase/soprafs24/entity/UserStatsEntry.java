package ch.uzh.ifi.hase.soprafs24.entity;

import ch.uzh.ifi.hase.soprafs24.constant.UserStatsStatus;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Document(collection = "UserStatsEntries")
public class UserStatsEntry {
    @Id
    private String id;

    @Indexed
    private String groupId;
    private String userId;
    private String habitId;
    private LocalDate dueDate;
    @Indexed
    private UserStatsStatus status;

    // Constructor
    public UserStatsEntry() {
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getHabitId() {
        return habitId;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public UserStatsStatus getStatus() {
        return status;
    }

    // Setters
    public void setId(String id) {
        this.id = id;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setHabitId(String habitId) {
        this.habitId = habitId;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public void setStatus(UserStatsStatus status) {
        this.status = status;
    }
}
