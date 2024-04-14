package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.entity.UserScore;
import ch.uzh.ifi.hase.soprafs24.service.UserScoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/userScores")
public class UserScoreController {

    @Autowired
    private UserScoreService service;

    @PostMapping
    public ResponseEntity<UserScore> createUserGroupStat(@RequestBody UserScore userScore) {
        return ResponseEntity.ok(service.addUserScore(userScore));
    }
}