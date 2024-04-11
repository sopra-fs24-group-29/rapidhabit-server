package ch.uzh.ifi.hase.soprafs24.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "Habits")
public class Habit {
    @Id
    private String id;
    private String name;
    private String description;
    private RepeatStrategy repeatStrategy;
    private int rewardPoints;


    public Habit(String id, String name, String description, RepeatStrategy repeatStrategy, int rewardPoints) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.repeatStrategy = repeatStrategy;
        this.rewardPoints = rewardPoints;
    }

    public Habit(String name, String description, int rewardPoints) {
        this.name = name;
        this.description = description;
        this.rewardPoints = rewardPoints;
    }

    // Getter und Setter
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public RepeatStrategy getRepeatStrategy() {
        return repeatStrategy;
    }

    public void setRepeatStrategy(RepeatStrategy repeatStrategy) {
        this.repeatStrategy = repeatStrategy;
    }

    public int getRewardPoints() {
        return rewardPoints;
    }

    public void setRewardPoints(int rewardPoints) {
        this.rewardPoints = rewardPoints;
    }
}
