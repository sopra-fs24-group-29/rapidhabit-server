package ch.uzh.ifi.hase.soprafs24.rest.dto.habit;

public class HabitPostDTO {

    private String name;
    private String description;
    private RepeatStrategyDTO repeatStrategy;
    private int rewardPoints;


    // Getter und Setter
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

    public RepeatStrategyDTO getRepeatStrategy() {
        return repeatStrategy;
    }

    public void setRepeatStrategy(RepeatStrategyDTO repeatStrategy) {
        this.repeatStrategy = repeatStrategy;
    }

    public int getRewardPoints() {
        return rewardPoints;
    }

    public void setRewardPoints(int rewardPoints) {
        this.rewardPoints = rewardPoints;
    }
}
