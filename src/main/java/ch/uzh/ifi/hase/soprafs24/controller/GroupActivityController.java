package ch.uzh.ifi.hase.soprafs24.controller;


import ch.uzh.ifi.hase.soprafs24.entity.*;
import ch.uzh.ifi.hase.soprafs24.rest.dto.group.*;
import ch.uzh.ifi.hase.soprafs24.service.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * User Controller
 * This class is responsible for handling all REST request that are related to
 * the user.
 * The controller will receive the request and delegate the execution to the
 * UserService and finally return the result.
 */
@RestController
public class GroupActivityController {

    private final GroupService groupService;
    private final AuthService authService;

    private final GroupActivityService groupActivityService;

    GroupActivityController(GroupService groupService, AuthService authService, UserStatsEntryService userStatsEntryService, HabitService habitService, GroupActivityService groupActivityService) {
        this.groupService = groupService;
        this.authService = authService;
        this.groupActivityService = groupActivityService;
    }

    @GetMapping("/groups/{groupId}/activity")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public ResponseEntity<?> getAllGroups(@RequestHeader("Authorization") String authToken, @PathVariable String groupId) {
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
        List<GroupActivityGetDTO> groupActivityGetDTOs = groupActivityService.getActivityData(groupId);
        return ResponseEntity.ok(groupActivityGetDTOs);
    }
}