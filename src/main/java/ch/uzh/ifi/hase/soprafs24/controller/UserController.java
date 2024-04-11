package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.rest.dto.user.*;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs24.service.AuthService;
import ch.uzh.ifi.hase.soprafs24.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

    private final AuthService authService;

    UserController(UserService userService, AuthService authService) {
        this.userService = userService;
        this.authService = authService;
    }

    @GetMapping("/users")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public ResponseEntity<?> getAllUsers(@RequestHeader("Authorization") String authHeader) {
        boolean isValid = authService.isTokenValid(authHeader);
        if(isValid){
            // fetch all users in the internal representation
            List<User> users = userService.getUsers();
            List<UserGetDTO> userGetDTOs = new ArrayList<>();

            // convert each user to the API representation
            for (User user : users) {
                userGetDTOs.add(DTOMapper.INSTANCE.convertEntityToUserGetDTO(user));
            }
            return ResponseEntity.ok(userGetDTOs);
        }
        else{
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
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

    @GetMapping("/users/{id}")
    public ResponseEntity<?> getDetailedUser(@RequestHeader("Authorization") String authHeader, @PathVariable String id) {
        boolean isValid = authService.isTokenValid(authHeader);
        if (isValid) {
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

    @GetMapping("/allowEdit")
    public ResponseEntity<?> allowEdit(@RequestHeader("Authorization") String authHeaderToken, @RequestParam String id){
        boolean isValid = authService.isTokenValid(authHeaderToken);
        if(isValid){
            User user = userService.getUserDetails(id);
            Boolean allowEdit = false;
            if(authService.getId(authHeaderToken).equals(user.getId())){
                allowEdit = true;
            }
            return ResponseEntity.ok(allowEdit);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();}
    @PutMapping ("/users/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String authHeaderToken){
        boolean isValid = authService.isTokenValid(authHeaderToken);
        String id = authService.getId(authHeaderToken);
        if(isValid){
            authService.logout(authHeaderToken);
            userService.setUserStatusToOffline(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
    @PutMapping("/users/{id}")
    public ResponseEntity<?> updateUser(@RequestHeader("Authorization") String authHeaderToken, @RequestBody UserPutDTO userPutDTO, @PathVariable String id) {
        boolean isValid = authService.isTokenValid(authHeaderToken);
        if (isValid) {
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
    @PutMapping("/users/{id}/password")
    public ResponseEntity<?> updateUserPassword(@RequestHeader("Authorization") String authHeaderToken, @RequestBody UserPasswordPutDTO userPasswordPutDTO, @PathVariable String id) {
        boolean isValid = authService.isTokenValid(authHeaderToken);
        if (isValid) {
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

    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@RequestHeader("Authorization") String authHeaderToken, @RequestBody UserPasswordPutDTO userPasswordPutDTO, @PathVariable String id) {
        boolean isValid = authService.isTokenValid(authHeaderToken);
        if (isValid) {
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
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            }
            // Altering the data of other users is prohibited!
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

}