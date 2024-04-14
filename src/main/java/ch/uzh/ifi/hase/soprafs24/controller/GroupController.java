package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.constant.RepeatType;
import ch.uzh.ifi.hase.soprafs24.constant.Weekday;
import ch.uzh.ifi.hase.soprafs24.entity.Group;
import ch.uzh.ifi.hase.soprafs24.entity.Habit;
import ch.uzh.ifi.hase.soprafs24.entity.UserStatsEntry;
import ch.uzh.ifi.hase.soprafs24.rest.dto.group.GroupGetDTO;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.rest.dto.user.*;
import ch.uzh.ifi.hase.soprafs24.rest.dto.group.GroupPostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.group.GroupJoinPostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.group.GroupPutDTO;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs24.scheduler.RoutineScheduler;
import ch.uzh.ifi.hase.soprafs24.service.AuthService;
import ch.uzh.ifi.hase.soprafs24.service.GroupService;
import ch.uzh.ifi.hase.soprafs24.service.HabitService;
import ch.uzh.ifi.hase.soprafs24.service.UserStatsEntryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

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


    private final HabitService habitService;

    private final UserStatsEntryService userStatsEntryService;

    GroupController(GroupService groupService, AuthService authService, RoutineScheduler routineScheduler, UserStatsEntryService userStatsEntryService, HabitService habitService) {
        this.groupService = groupService;
        this.authService = authService;
        this.userStatsEntryService = userStatsEntryService;
        this.habitService = habitService;
    }

    @GetMapping("/groups")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public ResponseEntity<?> getAllGroups(@RequestHeader("Authorization") String authHeader) {
        boolean isValid = authService.isTokenValid(authHeader);
        if(isValid){
            // fetch all users in the internal representation
            List<Group> group = groupService.getGroups();
            List<GroupGetDTO> groupGetDTOs = new ArrayList<>();

            // convert each user to the API representation
            for (Group group_ : group) {
                groupGetDTOs.add(DTOMapper.INSTANCE.convertEntityToGroupGetDTO(group_));
            }
            return ResponseEntity.ok(groupGetDTOs);
        }
        else{
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }


    @PostMapping("/groups") // defines a method to for handling post methods for creating new groups
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public ResponseEntity<?> createGroup(@RequestHeader("Authorization") String authHeader, @RequestBody GroupPostDTO groupPostDTO) {
        if(authService.isTokenValid(authHeader)){
            String userId = authService.getId(authHeader);
            System.out.println("POST Request received. Convert group to internal representation ...");
            if(groupPostDTO.getName().isEmpty()){
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
            // ...
            Group group = groupService.getGroupById(groupId);
            // Iterate through all habit IDs of the group, creating a new User Stats Entry for the corresponding user and the habit
            for (String habitId : group.getHabitIdList()){
                userStatsEntryService.createUserStatsEntry(userId, groupId, habitId);
            }
            return ResponseEntity.status(HttpStatus.CREATED).build();
        }
        else {
            String msg = "Invalid request token!";
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(msg);
        }
    }

    @PutMapping("/groups/{groupId}") // defines a method to for updating name and description of an existing group.
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public ResponseEntity<?> updateGroup(@RequestHeader("Authorization") String authHeader, @RequestBody GroupPutDTO groupPutDTO, @PathVariable String groupId) {
        if (authService.isTokenValid(authHeader)) {
            String userId = authService.getId(authHeader);
            System.out.println("PUT Request received. Convert group to internal representation ...");
            if (groupPutDTO.getName().isEmpty()) {
                String msg = "Group name must not be empty!";
                return ResponseEntity.badRequest().body(msg);
            }

            Group groupInput = DTOMapper.INSTANCE.convertGroupPutDTOtoEntity(groupPutDTO);
            System.out.println("Update group ...");

            Group updateGroup = groupService.updateGroup(groupInput, groupId);
            System.out.println("Convert internal representation back to API ...");
            return ResponseEntity.status(HttpStatus.OK).body(updateGroup);
        }
        else {
            String msg = "Invalid request token!";
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(msg);
            }
        }

    @DeleteMapping("/groups/{groupId}") // defines a method to for deleting a group.
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ResponseBody
    public ResponseEntity<?> deleteGroup(@RequestHeader("Authorization") String authHeader, @PathVariable String groupId) {
        if (authService.isTokenValid(authHeader)) {
            String userId = authService.getId(authHeader);
            System.out.println("Delete Request received. Preparing group for deletion.");

            if (!groupService.isUserAdmin(userId, groupId)) {
                return new ResponseEntity<>("User is not group admin", HttpStatus.UNAUTHORIZED);
            }
            Group delete_Group = groupService.deleteGroup(groupId);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();

        }
        else {
            String msg = "Invalid request token!";
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(msg);
        }
    }

    @DeleteMapping("/groups/{groupId}/users") // defines a method to for deleting a user from a group.
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ResponseBody
    public ResponseEntity<?> deleteUserFromGroup(@RequestHeader("Authorization") String authHeader, String userToRemoveID, @PathVariable String groupId) {
        if (authService.isTokenValid(authHeader)) {
            String userId = authService.getId(authHeader);
            System.out.println("Delete Request received. Preparing group for deletion.");

            if (!groupService.isUserAdmin(userId, groupId)) {
                return new ResponseEntity<>("User is not group admin", HttpStatus.UNAUTHORIZED);
            }
            Group delete_Group = groupService.removeUserFromGroup(groupId, userToRemoveID);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();

        }
        else {
            String msg = "Invalid request token!";
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(msg);
        }
    }

}