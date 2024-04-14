package ch.uzh.ifi.hase.soprafs24.rest.dto.group;

import java.util.List;

public class GroupGetDTO {

    private String id;
    private String name;
    private int streaks;
    private int currentRank;
    private List<String> userIds;
    private List<String> userInitials;

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

    public int getStreaks() {
        return streaks;
    }

    public void setStreaks(int streaks) {
        this.streaks = streaks;
    }

    public int getCurrentRank() {
        return currentRank;
    }

    public void setCurrentRank(int currentRank) {
        this.currentRank = currentRank;
    }

    public List<String> getUserIds() {
        return userIds;
    }

    public void setUserIds(List<String> userIds) {
        this.userIds = userIds;
    }

    public List<String> getUserInitials() {
        return userInitials;
    }

    public void setUserInitials(List<String> userInitials) {
        this.userInitials = userInitials;
    }
}
