package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.rest.dto.user.*;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs24.service.AuthService;
import ch.uzh.ifi.hase.soprafs24.service.GroupService;
import ch.uzh.ifi.hase.soprafs24.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * User Controller
 * This class is responsible for handling all REST request that are related to
 * the user.
 * The controller will receive the request and delegate the execution to the
 * UserService and finally return the result.
 */
@RestController
public class UserController {

    private final UserService userService;

    private final GroupService groupService;

    private final AuthService authService;

    UserController(UserService userService, AuthService authService, GroupService groupService) {
        this.userService = userService;
        this.authService = authService;
        this.groupService = groupService;
    }

    @PostMapping("/users") // defines a method to for handling post methods for creating new users
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public ResponseEntity<?> createUser(@RequestBody UserPostDTO userPostDTO) {
        System.out.println("POST Request received. Convert user to internal representation ...");
        if(userPostDTO.getFirstname().isEmpty() || userPostDTO.getLastname().isEmpty() || userPostDTO.getEmail().isEmpty() || userPostDTO.getPassword().isEmpty()){
            String msg = "None of the fields must be empty!";
            return ResponseEntity.badRequest().body(msg);
        }
        // convert API user to internal representation
        User userInput = DTOMapper.INSTANCE.convertUserPostDTOtoEntity(userPostDTO);
        System.out.println("Create user ...");
        // create user
        User createdUser = userService.createUser(userInput);
        System.out.println("Convert internal representation back to API ...");
        UserGetDTO userGetDTO = DTOMapper.INSTANCE.convertEntityToUserGetDTO(createdUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(userGetDTO);
    }
    @PutMapping("/users/login")
    public ResponseEntity<?> login(@RequestBody UserLoginPutDTO userLoginPutDTO){
        String token = authService.userLogin(userLoginPutDTO.getEmail(), userLoginPutDTO.getPassword());
        if (token != null){
            Map<String, String> tokenMap = new HashMap<>();
            tokenMap.put("token", token);
            return ResponseEntity.ok(tokenMap);
        }
        else {
            String msg = "Incorrect username or password";
            return ResponseEntity.badRequest().body(msg);
        }
    }

    @GetMapping("/users/profile")
    public ResponseEntity<?> getDetailedUser(@RequestHeader("Authorization") String authHeader) {
        boolean isValid = authService.isTokenValid(authHeader);
        if (isValid) {
            String id = authService.getId(authHeader);
            User user = userService.getUserDetails(id);
            if (user != null) {
                return ResponseEntity.ok(DTOMapper.INSTANCE.convertEntityToUserGetDTO(user));
            } else {
                String errorMessage = String.format("User with id " + id + " not found");
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", errorMessage);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
            }
        }
        else{ return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); }
    }

    @GetMapping("/users/id")
    public ResponseEntity<?> getId(@RequestHeader("Authorization") String authHeader) {
        boolean isValid = authService.isTokenValid(authHeader);
        if (isValid) {
            String id = authService.getId(authHeader);
            if (id != null) {
                return ResponseEntity.ok(id);
            } else {
                String errorMessage = String.format("User id not found");
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", errorMessage);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
            }
        }
        else{ return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); }
    }

    @PutMapping ("/users/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String authHeaderToken){
        boolean isValid = authService.isTokenValid(authHeaderToken);
        String id = authService.getId(authHeaderToken);
        if(isValid && id != null){
            authService.logout(authHeaderToken);
            userService.setUserStatusToOffline(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
    @PutMapping("/users/update")
    public ResponseEntity<?> updateUser(@RequestHeader("Authorization") String authHeaderToken, @RequestBody UserPutDTO userPutDTO) {
        boolean isValid = authService.isTokenValid(authHeaderToken);
        if (isValid) {
            String id = authService.getId(authHeaderToken);
            if (userPutDTO.getEmail().isEmpty() || userPutDTO.getFirstname().isEmpty() || userPutDTO.getLastname().isEmpty()){
                String msg = "None of the fields must be empty!";
                return ResponseEntity.badRequest().body(msg);
            }
            // check if the user with the token is the same user
            String tokenId = authService.getId(authHeaderToken);
            User userToEdit = userService.getUserDetails(id);
            String userIdToEdit =  userToEdit.getId();
            if (tokenId.equals(userIdToEdit)){
                User updatedUser = userService.updateUser(tokenId, userIdToEdit, userPutDTO);
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            }
            // Altering the data of other users is prohibited!
            ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
    @PutMapping("/users/password")
    public ResponseEntity<?> updateUserPassword(@RequestHeader("Authorization") String authHeaderToken, @RequestBody UserPasswordPutDTO userPasswordPutDTO) {
        boolean isValid = authService.isTokenValid(authHeaderToken);
        if (isValid) {
            String id = authService.getId(authHeaderToken);
            if (userPasswordPutDTO.getCurrentPassword().isEmpty() || userPasswordPutDTO.getNewPassword().isEmpty()){
                String msg = "None of the fields must be empty!";
                return ResponseEntity.badRequest().body(msg);
            }
            // check if the user with the token is the same user
            String tokenId = authService.getId(authHeaderToken);
            User userToEdit = userService.getUserDetails(id);
            String userIdToEdit =  userToEdit.getId();
            if (tokenId.equals(userIdToEdit)){
                User updatedUser = userService.updateUserPassword(userIdToEdit, userPasswordPutDTO);
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            }
            // Altering the data of other users is prohibited!
            ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @DeleteMapping("/users")
    public ResponseEntity<?> deleteUser(@RequestHeader("Authorization") String authHeaderToken, @RequestBody UserPasswordPutDTO userPasswordPutDTO) {
        boolean isValid = authService.isTokenValid(authHeaderToken);
        if (isValid) {
            String id = authService.getId(authHeaderToken);
            if (userPasswordPutDTO.getCurrentPassword().isEmpty()){
                String msg = "Current password must not be empty!";
                return ResponseEntity.badRequest().body(msg);
            }
            // Check if the user with the token is the same user
            String tokenId = authService.getId(authHeaderToken);
            User userToEdit = userService.getUserDetails(id);
            String userIdToEdit = userToEdit.getId();
            if (tokenId.equals(userIdToEdit)){
                User del_User = userService.delUser(userIdToEdit, userPasswordPutDTO);
                groupService.deleteUserIdFromAllGroups(id);
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            }
            // Altering the data of other users is prohibited!
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

}