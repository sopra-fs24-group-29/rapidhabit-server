package ch.uzh.ifi.hase.soprafs24.rest.dto.user;

public class UserPostDTO {
    private String firstname;
    private String lastname;
    private String email;

    private String password;

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


    public String getPassword(){return password;}

    public void setPassword(String password){
        this.password = password;
    }

}
