package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.constant.Weekday;
import ch.uzh.ifi.hase.soprafs24.entity.Group;
import ch.uzh.ifi.hase.soprafs24.service.AuthService;
import ch.uzh.ifi.hase.soprafs24.service.GroupService;
import ch.uzh.ifi.hase.soprafs24.service.UserService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import ch.uzh.ifi.hase.soprafs24.entity.Habit;
import ch.uzh.ifi.hase.soprafs24.entity.DailyRepeat;
import ch.uzh.ifi.hase.soprafs24.entity.WeeklyRepeat;
import ch.uzh.ifi.hase.soprafs24.service.HabitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@RestController
public class HabitController {

    private final HabitService habitService;

    private final GroupService groupService;

    private final UserService userService;
    private final ObjectMapper objectMapper;

    private final AuthService authService;


    @Autowired
    public HabitController(HabitService habitService, GroupService groupService, UserService userService, AuthService authService, ObjectMapper objectMapper) {
        this.habitService = habitService;
        this.objectMapper = objectMapper;
        this.groupService = groupService;
        this.userService = userService;
        this.authService = authService;
    }
    @PostMapping("/groups/{groupId}/habits")
    public ResponseEntity<?> createHabit(@RequestHeader("Authorization") String authHeaderToken,
                                         @PathVariable String groupId,
                                         @RequestBody String habitData) {
        boolean isValid = authService.isTokenValid(authHeaderToken);
        if (!isValid) {
            return new ResponseEntity<>("Invalid token", HttpStatus.UNAUTHORIZED);
        }

        String userId = authService.getId(authHeaderToken);
        // Check if user is group admin
        if (!groupService.isUserAdmin(userId, groupId)) {
            return new ResponseEntity<>("User is not group admin", HttpStatus.UNAUTHORIZED);
        }

        try {
            JsonNode rootNode = objectMapper.readTree(habitData);
            String name = rootNode.path("name").asText();
            String description = rootNode.path("description").asText();
            int rewardPoints = rootNode.path("rewardPoints").asInt();
            JsonNode repeatStrategyNode = rootNode.path("repeatStrategy");
            String repeatType = repeatStrategyNode.path("type").asText();

            Habit habit = new Habit(name, description, rewardPoints);
            if ("DAILY".equals(repeatType)) {
                habit.setRepeatStrategy(new DailyRepeat());
            } else if ("WEEKLY".equals(repeatType)) {
                JsonNode weekdayMapNode = repeatStrategyNode.path("weekdayMap");
                WeeklyRepeat weeklyRepeat = new WeeklyRepeat();
                // Iterate through each node of weekdayMapNode
                Iterator<Map.Entry<String, JsonNode>> fields = weekdayMapNode.fields();
                while (fields.hasNext()) {
                    Map.Entry<String, JsonNode> mapEntry = fields.next();
                    String day = mapEntry.getKey();
                    Boolean value = mapEntry.getValue().asBoolean();
                    try {
                        // Convert weekday strings to Weekday enums
                        Weekday weekday = Weekday.valueOf(day.toUpperCase());
                        // If weekday in JSON is true, set weekday in object to true as well
                        weeklyRepeat.setWeekdayToRepeat(weekday, value);
                    } catch (IllegalArgumentException e) {
                        // Log error or handle it accordingly
                        return new ResponseEntity<>("Invalid weekday name: " + day, HttpStatus.BAD_REQUEST);
                    }
                }
                habit.setRepeatStrategy(weeklyRepeat);
            }
            // create habit within the db
            Habit createdHabit = habitService.createHabit(habit);
            // connect habit to group within the db
            groupService.addHabitToGroup(groupId, habit.getId());
            return new ResponseEntity<>(createdHabit, HttpStatus.CREATED);

        } catch (IOException e) {
            return new ResponseEntity<>("Error parsing JSON", HttpStatus.BAD_REQUEST);
        }
    }

}
