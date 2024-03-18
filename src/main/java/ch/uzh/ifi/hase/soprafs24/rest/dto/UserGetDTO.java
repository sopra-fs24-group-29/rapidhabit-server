package ch.uzh.ifi.hase.soprafs24.rest.dto;

import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;


public class UserGetDTO {

    private Long id;
    private String username;
    private LocalDateTime creationDate;

    private UserStatus status;

    private LocalDate birthdate;

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public LocalDateTime getCreationDate(){return creationDate;}

    public void setCreationDate(LocalDateTime date){this.creationDate = date;}

    public UserStatus getStatus() {
        return status;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }


    public void setBirthdate(LocalDate birthdate){this.birthdate = birthdate;}

    public LocalDate getBirthdate(){return birthdate;}
}

