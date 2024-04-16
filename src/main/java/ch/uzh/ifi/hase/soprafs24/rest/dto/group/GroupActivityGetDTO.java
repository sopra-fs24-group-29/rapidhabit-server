package ch.uzh.ifi.hase.soprafs24.rest.dto.group;

import ch.uzh.ifi.hase.soprafs24.constant.GroupActivityStatus;

import java.time.LocalDate;

public class GroupActivityGetDTO {
    private LocalDate date;
    private GroupActivityStatus status;

    public void setDate(LocalDate date){
        this.date = date;
    }
    public LocalDate getDate(){
        return this.date;
    }
    public void setStatus(GroupActivityStatus status){
        this.status = status;
    }
    public GroupActivityStatus getStatus(){
        return this.status;
    }
}
