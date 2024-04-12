package ch.uzh.ifi.hase.soprafs24.entity;

import ch.uzh.ifi.hase.soprafs24.constant.UserStatsStatus;
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
    private List<String> userIdList;

    private List<String> habitIdList;

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
        return this.userIdList;
    }

    public void setUserIdList(List<String> userIdList) {
        this.userIdList = userIdList;
    }
    public void addUserId(String userId) {
        this.userIdList.add(userId);
    }

    public List<String> getHabitIdList() {
        return this.habitIdList;
    }

    public void setHabitIdList(List<String> habitIdList) {
        this.habitIdList = habitIdList;
    }
}
