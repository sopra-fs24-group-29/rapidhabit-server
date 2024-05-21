package ch.uzh.ifi.hase.soprafs24.rest.dto.user;
import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;


public class UserGetDTO {

    private String id;
    private String firstname;
    private String lastname;
    private String email;
    private UserStatus status;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }


    public UserStatus getStatus() {
        return status;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }
}