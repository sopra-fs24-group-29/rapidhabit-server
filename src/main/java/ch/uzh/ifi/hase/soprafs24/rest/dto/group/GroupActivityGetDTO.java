package ch.uzh.ifi.hase.soprafs24.rest.dto.group;

import java.time.LocalDate;

public class GroupActivityGetDTO {
    private LocalDate date;
    private Boolean success;

    public void setDate(LocalDate date){
        this.date = date;
    }
    public LocalDate getDate(){
        return this.date;
    }
    public void setSuccess(Boolean success){
        this.success = success;
    }
    public Boolean getSuccess(){
        return this.success;
    }
}
