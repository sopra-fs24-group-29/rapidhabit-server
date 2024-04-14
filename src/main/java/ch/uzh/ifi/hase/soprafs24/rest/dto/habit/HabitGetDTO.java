package ch.uzh.ifi.hase.soprafs24.rest.dto.habit;

import java.util.HashMap;
import java.util.Map;

public class HabitGetDTO {
    private String id;
    private String name;
    private int streaks;
    private Boolean checked;
    private Map<String, Boolean> userCheckStatus = new HashMap<>();

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

    public Boolean getChecked() {
        return checked;
    }

    public void setChecked(Boolean checked) {
        this.checked = checked;
    }

    public Map<String, Boolean> getUserCheckStatus() {
        return userCheckStatus;
    }

    public void setUserCheckStatus(Map<String, Boolean> userCheckStatus) {
        this.userCheckStatus = userCheckStatus;
    }
}
