package ch.uzh.ifi.hase.soprafs24.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "GroupStatistics")
public class GroupStatistics {
    @Id
    private String id;
    @Indexed(unique = true)
    private String groupId;
    private List<String> userStatsEntryIds; // Updated from userData
    private List<String> habitStatsEntryIds; // Updated from habitData
    private List<String> userHabitStatsEntryIds; // Updated from userHabitData
    private List<String> pulseCheckEntryIds; // Updated from pulseCheckData
    private List<String> streakEntryIds; // Updated from streakData
    private List<String> superStreakEntryIds; // Updated from superStreakData
    private List<String> feedEntryIds; // Updated from feedData
    private List<String> chatEntryIds; // Updated from chatData

    public GroupStatistics(){
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

    public List<String> getUserStatsEntryIds() {
        return userStatsEntryIds;
    }

    public void setUserStatsEntryIds(List<String> userStatsEntryIds) {
        this.userStatsEntryIds = userStatsEntryIds;
    }

    public List<String> getHabitStatsEntryIds() {
        return habitStatsEntryIds;
    }

    public void setHabitStatsEntryIds(List<String> habitStatsEntryIds) {
        this.habitStatsEntryIds = habitStatsEntryIds;
    }

    public List<String> getUserHabitStatsEntryIds() {
        return userHabitStatsEntryIds;
    }

    public void setUserHabitStatsEntryIds(List<String> userHabitStatsEntryIds) {
        this.userHabitStatsEntryIds = userHabitStatsEntryIds;
    }

    public List<String> getPulseCheckEntryIds() {
        return pulseCheckEntryIds;
    }

    public void setPulseCheckEntryIds(List<String> pulseCheckEntryIds) {
        this.pulseCheckEntryIds = pulseCheckEntryIds;
    }

    public List<String> getStreakEntryIds() {
        return streakEntryIds;
    }

    public void setStreakEntryIds(List<String> streakEntryIds) {
        this.streakEntryIds = streakEntryIds;
    }

    public List<String> getSuperStreakEntryIds() {
        return superStreakEntryIds;
    }

    public void setSuperStreakEntryIds(List<String> superStreakEntryIds) {
        this.superStreakEntryIds = superStreakEntryIds;
    }

    public List<String> getFeedEntryIds() {
        return feedEntryIds;
    }

    public void setFeedEntryIds(List<String> feedEntryIds) {
        this.feedEntryIds = feedEntryIds;
    }

    public List<String> getChatEntryIds() {
        return chatEntryIds;
    }

    public void setChatEntryIds(List<String> chatEntryIds) {
        this.chatEntryIds = chatEntryIds;
    }
}
