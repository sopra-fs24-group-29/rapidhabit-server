package ch.uzh.ifi.hase.soprafs24.rest.dto;

public class UserPasswordPutDTO {
    private String currentPassword;
    private String newPassword;

    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

}