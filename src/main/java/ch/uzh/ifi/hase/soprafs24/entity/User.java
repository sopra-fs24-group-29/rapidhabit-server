package ch.uzh.ifi.hase.soprafs24.entity;

import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;


/**
 * Internal User Representation
 * This class composes the internal representation of the user and defines how
 * the user is stored in the database.
 * Every variable will be mapped into a database field with the @Column
 * annotation
 * - nullable = false -> this cannot be left empty
 * - unique = true -> this value must be unqiue across the database -> composes
 * the primary key
 */
@Entity
@Table(name = "USER")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false, updatable = false)
    private LocalDateTime creationDate;

    @Column(nullable = false, unique = true)
    private String username;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column()
    private String name;

    @Column(nullable = false)
    private String password;


    @Column(nullable = false)
    private UserStatus status;

    @Column()
    private LocalDate birthdate;

    public Long getId() {
        return id;
    }

    @PrePersist // executed when User is created
    public void prePersist() {
        this.creationDate = LocalDateTime.now();
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPassword(){
        return password;
    }

    public void setPassword(String password){
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }


    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public UserStatus getStatus() {
        return status;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }

    public void setBirthdate(LocalDate localDate){this.birthdate = localDate;}
    public LocalDate getBirthdate(){return this.birthdate;}

    public void emitInfo() {
        System.out.println("User Information:");
        System.out.println("ID: " + id);
        System.out.println("Creation Date: " + creationDate);
        System.out.println("Username: " + username);
        System.out.println("Status: " + status);
    }


}
