package ch.uzh.ifi.hase.soprafs24.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

@Document(collection = "HabitStreaks")
public class HabitStreak {

    @Id
    private String id;

    @Indexed
    private String groupId; // To quickly fetch all HabitStreaks for a specific group

    private String habitId;

    private int streak; // To store the count of the current streak for the habit

    // Constructors
    public HabitStreak() {
    }

    public HabitStreak(String groupId, String habitId) {
        this.groupId = groupId;
        this.habitId = habitId;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public void setHabitId(String habitId) {
        this.habitId = habitId;
    }

    public int getStreak() {
        return streak;
    }

    public void setStreak(int streak) {
        this.streak = streak;
    }

    // debugging method
    @Override
    public String toString() {
        return "HabitStreak{" +
                "id='" + id + '\'' +
                ", groupId='" + groupId + '\'' +
                ", streak=" + streak +
                '}';
    }
}
