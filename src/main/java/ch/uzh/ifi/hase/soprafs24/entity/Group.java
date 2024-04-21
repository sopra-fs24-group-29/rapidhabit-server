package ch.uzh.ifi.hase.soprafs24.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "Groups")
public class Group {
    @Id
    private String id;
    private String name;
    private String description;

    private List<String> adminIdList;
    private String accessCode;
    private List<String> userIds;

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
