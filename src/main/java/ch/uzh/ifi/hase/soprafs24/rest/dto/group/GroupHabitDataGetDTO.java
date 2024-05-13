package ch.uzh.ifi.hase.soprafs24.rest.dto.group;

import ch.uzh.ifi.hase.soprafs24.constant.UserStatsStatus;

import java.time.LocalDate;
import java.util.Map;

public class GroupHabitDataGetDTO {
    private String name;
    private String description;
    private int currentTeamStreak;
    private Map<LocalDate, Map<String, UserStatsStatus>> statusMap;

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }
    public String getDescription(){
        return description;
    }
    public void setDescription(String description){
        this.description = description;
    }

    public int getCurrentTeamStreak() {
        return currentTeamStreak;
    }

    public void setCurrentTeamStreak(int currentTeamStreak) {
        this.currentTeamStreak = currentTeamStreak;
    }

    public Map<LocalDate, Map<String, UserStatsStatus>> getStatusMap() {
        return statusMap;
    }

    public void setStatusMap(Map<LocalDate, Map<String, UserStatsStatus>> statusMap) {
        this.statusMap = statusMap;
    }
}

