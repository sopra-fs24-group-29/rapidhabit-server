package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.service.AuthService;
import ch.uzh.ifi.hase.soprafs24.service.GroupService;
import ch.uzh.ifi.hase.soprafs24.service.UserScoreService;
import ch.uzh.ifi.hase.soprafs24.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class UserScoreController {

    @Autowired
    AuthService authService;

    @Autowired
    GroupService groupService;

    @Autowired
    UserService userService;

    @Autowired
    UserScoreService userScoreService;

    @GetMapping("/groups/{groupId}/scores")
    public ResponseEntity<?> getGroupScores(@RequestHeader("Authorization") String authHeader, @PathVariable String groupId) {
        boolean isValid = authService.isTokenValid(authHeader);
        if (!isValid) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }
        String userId = authService.getId(authHeader);
        if (!groupService.getUserIdsByGroupId(groupId).contains(userId)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User is not part of this group.");
        }
        List<String> userIds = groupService.getUserIdsByGroupId(groupId);
        List<Map<String, Object>> scoreList = new ArrayList<>();

        for (String memberId : userIds) {
            int userScore = userScoreService.getUserScoreObjekt(memberId, groupId).getPoints();
            String memberInitials = userService.getInitials(memberId);

            Map<String, Object> scoreData = new HashMap<>();
            scoreData.put("userId", memberId);
            scoreData.put("userInitials", memberInitials);
            scoreData.put("points", userScore);
            scoreList.add(scoreData);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("scores", scoreList);
        return ResponseEntity.ok(response);
    }
}