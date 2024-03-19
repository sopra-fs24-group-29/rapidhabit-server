package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.rest.dto.*;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs24.service.AuthService;
import ch.uzh.ifi.hase.soprafs24.service.UserService;
import org.springframework.data.repository.query.Param;
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
        if(userPostDTO.getUsername().equals("") || userPostDTO.getPassword().equals("")){
            String msg = "Username or password must not be empty!";
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
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody UserPostDTO userPostDTO){
        String token = authService.userLogin(userPostDTO.getUsername(), userPostDTO.getPassword());
        if (token != null){
            return ResponseEntity.ok(token);
        }
        else {
            String msg = "Incorrect username or password";
            return ResponseEntity.badRequest().body(msg);
        }
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<?> getDetailedUser(@RequestHeader("Authorization") String authHeader, @PathVariable Long id) {
        boolean isValid = authService.isTokenValid(authHeader);
        if (isValid) {
            User user = userService.getUserDetails(id);
            if (user != null) {
                return ResponseEntity.ok(DTOMapper.INSTANCE.convertEntityToUserGetDTO(user));
            } else {
                String errorMessage = String.format("User with id %d was not found", id);
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", errorMessage);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
            }
        }
        else{ return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); }
    }

    @GetMapping("/allowEdit")
    public ResponseEntity<?> allowEdit(@RequestHeader("Authorization") String authHeader, @RequestParam Long id){
        boolean isValid = authService.isTokenValid(authHeader);
        if(isValid){
            User user = userService.getUserDetails(id);
            Boolean allowEdit = false;
            if(authService.getUsername(authHeader).equals(user.getUsername())){
                allowEdit = true;
            }
            return ResponseEntity.ok(allowEdit);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();}
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String authHeader){
        boolean isValid = authService.isTokenValid(authHeader);
        String username = authService.getUsername(authHeader);
        if(isValid){
            authService.logout(authHeader);
            userService.setUserStatusToOffline(username);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
    @PutMapping("/users/{id}")
    public ResponseEntity<?> updateUser(@RequestHeader("Authorization") String authHeader, @RequestBody UserPutDTO userPutDTO, @PathVariable Long id) {
        boolean isValid = authService.isTokenValid(authHeader);
        if (isValid) {
            if (userPutDTO.getUsername() == ""){
                String msg = "Username must not be empty!";
                return ResponseEntity.badRequest().body(msg);
            }
            // check if the user with the token is the same user
            String username_token = authService.getUsername(authHeader);
            String username_to_edit =  userService.getUserDetails(id).getUsername();
            if (username_token.equals(username_to_edit)){
                User updatedUser = userService.updateUser(username_token, userPutDTO);
                if (username_token != userPutDTO.getUsername()){
                    authService.setUsername(authHeader, userPutDTO.getUsername());
                }
                return ResponseEntity.noContent().build();
            }
            // Altering the data of other users is prohibited!
            ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}