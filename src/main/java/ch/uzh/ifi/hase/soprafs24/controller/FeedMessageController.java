package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.entity.FeedMessage;
import ch.uzh.ifi.hase.soprafs24.rest.dto.feed.FeedMessagePulseCheckPutDTO;
import ch.uzh.ifi.hase.soprafs24.service.AuthService;
import ch.uzh.ifi.hase.soprafs24.service.FeedMessageService;
import ch.uzh.ifi.hase.soprafs24.service.GroupService;
import ch.uzh.ifi.hase.soprafs24.service.PulseCheckEntryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class FeedMessageController {

    private final AuthService authService;
    private final GroupService groupService;

    private final FeedMessageService feedMessageService;

    private final PulseCheckEntryService pulseCheckEntryService;



    FeedMessageController(AuthService authService, GroupService groupService, FeedMessageService feedMessageService, PulseCheckEntryService pulseCheckEntryService){
        this.authService = authService;
        this.groupService = groupService;
        this.feedMessageService = feedMessageService;
        this.pulseCheckEntryService = pulseCheckEntryService;
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
    @PutMapping("/groups/{groupId}/feed/pulsecheck") // Endpoint to update pulse check responses
    public ResponseEntity<?> putPulseCheckResponse(
            @RequestBody FeedMessagePulseCheckPutDTO feedMessagePulseCheckPutDTO,
            @RequestHeader("Authorization") String authToken,
            @PathVariable String groupId) {

        System.out.println("REQUEST RECEIVED ...");
        System.out.println("...");
        if (!authService.isTokenValid(authToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String userId = authService.getId(authToken);
        if (!groupService.getGroupById(groupId).getUserIdList().contains(userId)){ // check if user is part of group
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        System.out.println("User is authorized!");
        String formId = feedMessagePulseCheckPutDTO.getFormId();
        System.out.println("Get latest Pulse Check Message of Group ...");
        FeedMessage feedMessage = feedMessageService.getLatestPulseCheckMessage(groupId,formId); // retrieve latest pulse check feed message from group with groupId
        String feedId = feedMessage.getId();

        try {
            System.out.println("Update value is:");
            Double value = feedMessagePulseCheckPutDTO.getValue(); // retrieve update value
            System.out.println(value);
            System.out.println("Updating PulseCheckEntry ...");
            pulseCheckEntryService.updateEntryByUserId(userId, formId, value); // includes validity check with regard to submissionDeadline
            System.out.println("Update successful.");
            System.out.println("Add User Submit ...");
            feedMessageService.addUserSubmit(feedId, userId, value);
            System.out.println("Update successful.");
            System.out.println("Get Feed MSG ...");
            feedMessage = feedMessageService.getById(feedId);
            System.out.println("Return response ...");
            return ResponseEntity.ok(feedMessage);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
}