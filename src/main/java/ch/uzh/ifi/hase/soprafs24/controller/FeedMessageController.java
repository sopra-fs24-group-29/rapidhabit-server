package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.entity.FeedMessage;
import ch.uzh.ifi.hase.soprafs24.entity.Group;
import ch.uzh.ifi.hase.soprafs24.service.AuthService;
import ch.uzh.ifi.hase.soprafs24.service.FeedMessageService;
import ch.uzh.ifi.hase.soprafs24.service.GroupService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class FeedMessageController {

    private final AuthService authService;
    private final GroupService groupService;

    private final FeedMessageService feedMessageService;



    FeedMessageController(AuthService authService, GroupService groupService, FeedMessageService feedMessageService){
        this.authService = authService;
        this.groupService = groupService;
        this.feedMessageService = feedMessageService;
    }

    @GetMapping("/feed") // retrieves latest feed messages
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public ResponseEntity<?> getSpecificGroupUsers(@RequestHeader("Authorization") String authToken) {
        boolean isValid = authService.isTokenValid(authToken);
        if(isValid){
            String userId = authService.getId(authToken);
            List<String> groupIdList = groupService.getGroupIdsByUserId(userId); // Retrieve all group ids from user
            List<FeedMessage> feedMessages = feedMessageService.findLatestFeedMessagesByGroupIds(groupIdList, 20); // retrieves 20 last messages from these groups
            return ResponseEntity.ok(feedMessages);
        }
        else{
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}
