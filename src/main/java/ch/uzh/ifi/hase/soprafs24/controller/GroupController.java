package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.entity.Group;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.rest.dto.*;
import ch.uzh.ifi.hase.soprafs24.rest.dto.group.GroupPostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.group.GroupJoinPostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs24.service.AuthService;
import ch.uzh.ifi.hase.soprafs24.service.GroupService;
import ch.uzh.ifi.hase.soprafs24.service.UserService;
import org.apache.catalina.SessionListener;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * User Controller
 * This class is responsible for handling all REST request that are related to
 * the user.
 * The controller will receive the request and delegate the execution to the
 * UserService and finally return the result.
 */
@RestController
public class GroupController {

    private final GroupService groupService;

    private final AuthService authService;

    GroupController(GroupService groupService, AuthService authService) {
        this.groupService = groupService;
        this.authService = authService;
    }

    @PostMapping("/groups") // defines a method to for handling post methods for creating new users
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public ResponseEntity<?> createUser(@RequestHeader("Authorization") String authHeader, @RequestBody GroupPostDTO groupPostDTO) {
        if(authService.isTokenValid(authHeader)){
            String userId = authService.getId(authHeader);
            System.out.println("POST Request received. Convert group to internal representation ...");
            if(groupPostDTO.getName().equals("")){
                String msg = "Group name must not be empty!";
                return ResponseEntity.badRequest().body(msg);
            }
            // convert API user to internal representation
            Group groupInput = DTOMapper.INSTANCE.convertGroupPostDTOtoEntity(groupPostDTO);
            System.out.println("Create group ...");
            // create user
            Group createdGroup = groupService.createGroup(groupInput, userId);
            System.out.println("Convert internal representation back to API ...");
            return ResponseEntity.status(HttpStatus.CREATED).body(createdGroup);
    }
        else {
            String msg = "Invalid request token!";
            return ResponseEntity.badRequest().body(msg);
        }
    }

    @PostMapping("/groups/{groupId}/users") // defines a method to for handling post methods for creating new users
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public ResponseEntity<?> createUser(@RequestHeader("Authorization") String authHeader, @RequestBody GroupJoinPostDTO groupJoinPostDTO, @PathVariable String groupId) {
        if(authService.isTokenValid(authHeader)){
            String userId = authService.getId(authHeader);
            System.out.println("POST Request received. Convert group to internal representation ...");
            groupService.addUserByAccessCode(groupId, userId, groupJoinPostDTO.getAccessKey());
            return ResponseEntity.status(HttpStatus.CREATED).build();
        }
        else {
            String msg = "Invalid request token!";
            return ResponseEntity.badRequest().body(msg);
        }
    }
}