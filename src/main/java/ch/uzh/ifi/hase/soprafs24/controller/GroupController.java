package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.entity.*;
import ch.uzh.ifi.hase.soprafs24.rest.dto.group.*;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs24.service.*;
import ch.uzh.ifi.hase.soprafs24.util.WeekdayUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
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

    private final UserScoreService userScoreService;

    private final UserService userService;

    GroupController(GroupService groupService, AuthService authService, UserService userService, UserStatsEntryService userStatsEntryService, HabitService habitService, UserScoreService userScoreService) {
        this.groupService = groupService;
        this.authService = authService;
        this.userService = userService;
        this.userStatsEntryService = userStatsEntryService;
        this.habitService = habitService;
        this.userScoreService = userScoreService;
    }

    @GetMapping("/groups")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public ResponseEntity<?> getAllGroups(@RequestHeader("Authorization") String authToken) {
        boolean isValid = authService.isTokenValid(authToken);
        if(isValid){
            // fetch userId of the person who did the request
            String userId = authService.getId(authToken);
            List<GroupGetDTO> groupGetDTOList = groupService.getGroupMenuDataByUserId(userId);
            return ResponseEntity.ok(groupGetDTOList);
        }
        else{
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @GetMapping("/groups/{groupId}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public ResponseEntity<?> getSpecificGroup(@RequestHeader("Authorization") String authToken, @PathVariable String groupId) {
        boolean isValid = authService.isTokenValid(authToken);
        if(isValid){
            String userId = authService.getId(authToken);
            boolean isAdmin = groupService.isUserAdmin(userId, groupId);
            if (isAdmin) {
                Group group = groupService.getGroupById(groupId);
                return ResponseEntity.ok(group);
            }
            else{
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
        }
        else{
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @GetMapping("/groups/{groupId}/ranking")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public ResponseEntity<?> getRanking(@RequestHeader("Authorization") String authToken, @PathVariable String groupId) {
        boolean isValid = authService.isTokenValid(authToken);
        if (!isValid) {
            return new ResponseEntity<>("Invalid token", HttpStatus.UNAUTHORIZED);
        }
        // check who did the request
        String userId = authService.getId(authToken);
        Group group = groupService.getGroupById(groupId);
        //check if user is part of group
        if (!group.getUserIdList().contains(userId)) {
            return new ResponseEntity<>("User is not part of this group", HttpStatus.UNAUTHORIZED);
        }
        List<GroupRankingGetDTO> groupRankingGetDTOs = new ArrayList<>();
        for (String groupMemberId : group.getUserIdList()){
            int userRank = userScoreService.getUserScore(groupMemberId, groupId);
            String userInitials = userService.getInitials(groupMemberId);
            // wrap data into DTO
            GroupRankingGetDTO groupRankingGetDTO = new GroupRankingGetDTO();
            groupRankingGetDTO.setId(groupMemberId);
            groupRankingGetDTO.setInitials(userInitials);
            groupRankingGetDTO.setRank(userRank);
            groupRankingGetDTOs.add(groupRankingGetDTO);
        }
        return ResponseEntity.ok(groupRankingGetDTOs);
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
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(msg);      }
    }

    @PostMapping("/groups/join") // defines a method to for handling post methods for creating new users
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public ResponseEntity<?> createUser(@RequestHeader("Authorization") String authHeader, @RequestBody GroupJoinPostDTO groupJoinPostDTO) {
        if(authService.isTokenValid(authHeader)){
            String userId = authService.getId(authHeader);
            System.out.println("POST Request received. Convert group to internal representation ...");
            groupService.addUserByAccessCode(userId, groupJoinPostDTO.getAccessKey());
            Group group = groupService.getGroupByAccessCode(groupJoinPostDTO.getAccessKey());
            String groupId = group.getId();
            // Iterate through all habit IDs of the group, creating a new User Stats Entry for the corresponding user and the habit
            for (String habitId : group.getHabitIdList()){
                //
                Habit habit = habitService.getHabitById(habitId)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                "No habit with id " + userId + " was found."));
                if(habit.getRepeatStrategy().repeatsAt(WeekdayUtil.getCurrentWeekday())){
                    userStatsEntryService.createUserStatsEntry(userId, groupId, habitId);
                }
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
            groupService.deleteGroup(groupId);
            userScoreService.deleteByGroupId(groupId);
            userStatsEntryService.deleteByGroupId(groupId);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();

        }
        else {
            String msg = "Invalid request token!";
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(msg);
        }
    }

    @DeleteMapping("/groups/{groupId}/users/userId") // defines a method to for deleting a user from a group.
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ResponseBody
    public ResponseEntity<?> deleteUserFromGroup(@RequestHeader("Authorization") String authHeader, @PathVariable String groupId, @PathVariable String userId) {
        String userToRemoveID = userId;
        if (authService.isTokenValid(authHeader)) {
            userId = authService.getId(authHeader);
            System.out.println("Delete Request received. Preparing group for deletion.");
            if(userId.equals(userToRemoveID)){
                return new ResponseEntity<>("You can only delete other memebers from the group.", HttpStatus.BAD_REQUEST);
            }

            if (!groupService.isUserAdmin(userId, groupId)) {
                return new ResponseEntity<>("User is not group admin", HttpStatus.UNAUTHORIZED);
            }
            Group delete_Group = groupService.removeUserFromGroup(groupId, userToRemoveID); // remove the user from the userIdList within the group
            userScoreService.deleteByUserIdAndGroupId(userToRemoveID, groupId); // Remove all entries from UserScores associated to the user within the group
            userScoreService.updateRanksInGroup(groupId); // Update rank of remaining group members
            userStatsEntryService.deleteByUserIdAndDueDate(userId, LocalDate.now()); // Today's UserStats Entries from this user will be removed, historical data remains.
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        else {
            String msg = "Invalid request token!";
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(msg);
        }
    }

}