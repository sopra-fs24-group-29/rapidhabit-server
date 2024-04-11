package ch.uzh.ifi.hase.soprafs24.entity;

import ch.uzh.ifi.hase.soprafs24.constant.UserStatsStatus;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Document(collection = "UserStatsEntries")
public class UserStatsEntry {
    @Id
    private String id;
    private String userId;
    private String habitId;
    private LocalDateTime dueDate;
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

    public String getHabitId() {
        return habitId;
    }

    public LocalDateTime getDueDate() {
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

    public void setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
    }

    public void setStatus(UserStatsStatus status) {
        this.status = status;
    }
}
