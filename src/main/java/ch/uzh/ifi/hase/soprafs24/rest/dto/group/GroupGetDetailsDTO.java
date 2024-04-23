package ch.uzh.ifi.hase.soprafs24.rest.dto.group;

import java.util.List;

public class GroupGetDetailsDTO {
    private String id;
    private String name;
    private String description;

    private List<String> adminIdList;
    private String accessCode;
    private List<String> userIds;

    private List<String> userInitials;

    private List<String> habitIds;

    private int currentStreak;

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<String> getAdminIdList(){
        return this.adminIdList;
    }

    public void setAdminIdList(List<String> adminIdList) {
        this.adminIdList = adminIdList;
    }

    public void addAdminId(String adminId){
        this.adminIdList.add(adminId);
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAccessCode() {
        return this.accessCode;
    }

    public void setAccessCode(String accessCode) {
        this.accessCode = accessCode;
    }

    public List<String> getUserIdList() {
        return this.userIds;
    }

    public void setUserIdList(List<String> userIds) {
        this.userIds = userIds;
    }

    public void addUserId(String userId) {
        this.userIds.add(userId);
    }

    public List<String> getUserInitials() {
        return this.userInitials;
    }

    public void setUserInitials(List<String> userInitials) {
        this.userInitials = userInitials;
    }

    public void removeUserId(String userId) {
        this.userIds.remove(userId);
    }

    public void removeHabitID(String habitId){
        this.habitIds.remove(habitId);
    }

    public void removeAdminId(String adminId){
        this.adminIdList.remove(adminId);
    }

    public List<String> getHabitIdList() {
        return this.habitIds;
    }

    public void setHabitIdList(List<String> habitIds) {
        this.habitIds = habitIds;
    }

    public int getCurrentStreak() {
        return this.currentStreak;
    }

    public void setCurrentStreak(int currentStreak) {
        this.currentStreak = currentStreak;
    }
}
