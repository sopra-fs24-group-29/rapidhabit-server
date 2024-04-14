package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.entity.HabitStreak;
import ch.uzh.ifi.hase.soprafs24.service.HabitStreakService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/habitStreaks")
public class HabitStreakController {

    @Autowired
    private HabitStreakService service;

    @PostMapping
    public ResponseEntity<HabitStreak> createHabitStreak(@RequestBody HabitStreak habitStreak) {
        return ResponseEntity.ok(service.addHabitStreak(habitStreak));
    }
}
