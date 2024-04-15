package ch.uzh.ifi.hase.soprafs24.rest.dto.habit;

import ch.uzh.ifi.hase.soprafs24.constant.UserStatsStatus;

import java.time.LocalDate;

public class HabitDateUserStatusGetDTO {
    private LocalDate date;
    private String userId;
    private UserStatsStatus status;

    public HabitDateUserStatusGetDTO(LocalDate date, String userId, UserStatsStatus status) {
        this.date = date;
        this.userId = userId;
        this.status = status;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public UserStatsStatus getStatus() {
        return status;
    }

    public void setStatus(UserStatsStatus status) {
        this.status = status;
    }
}

