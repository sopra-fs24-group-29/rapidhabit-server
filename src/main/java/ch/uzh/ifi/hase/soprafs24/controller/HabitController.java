package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.constant.RepeatType;
import ch.uzh.ifi.hase.soprafs24.constant.UserStatsStatus;
import ch.uzh.ifi.hase.soprafs24.constant.Weekday;
import ch.uzh.ifi.hase.soprafs24.entity.*;
import ch.uzh.ifi.hase.soprafs24.rest.dto.group.GroupHabitDataGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.habit.HabitGetDTO;
import ch.uzh.ifi.hase.soprafs24.service.*;
import ch.uzh.ifi.hase.soprafs24.util.WeekdayUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

@RestController
public class HabitController {

    private final HabitService habitService;

    private final GroupService groupService;

    private final UserService userService;
    private final ObjectMapper objectMapper;

    private final AuthService authService;

    private final UserStatsEntryService userStatsEntryService;

    @Autowired
    public HabitController(HabitService habitService, GroupService groupService, UserService userService, AuthService authService, ObjectMapper objectMapper, UserStatsEntryService userStatsEntryService) {
        this.habitService = habitService;
        this.objectMapper = objectMapper;
        this.groupService = groupService;
        this.userService = userService;
        this.authService = authService;
        this.userStatsEntryService = userStatsEntryService;
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
                        return new ResponseEntity<>("Invalid weekday name: " + day, HttpStatus.BAD_REQUEST);
                    }
                }
                habit.setRepeatStrategy(weeklyRepeat);
            }
            // create habit within the db
            Habit createdHabit = habitService.createHabit(habit);
            // connect habit to group within the db
            groupService.addHabitIdToGroup(groupId, habit.getId());
            Group group = groupService.getGroupById(groupId);
            // check if habit needs to be scheduled for current weekday
            scheduleHabitRoutines(group, habit);
            return new ResponseEntity<>(createdHabit, HttpStatus.CREATED);

        } catch (IOException e) {
            return new ResponseEntity<>("Error parsing JSON", HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/groups/{groupId}/habits")
    public ResponseEntity<?> getHabits(@RequestHeader("Authorization") String authToken, @PathVariable String groupId) {
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

        List<HabitGetDTO> habitDTOs = new ArrayList<>();
        // Go through each habit of the group
        List<String> habitIds = group.getHabitIdList();
        for (String habitId : habitIds) {
            Habit habit = habitService.getHabitById(habitId).orElseThrow(() ->
                    new ResponseStatusException(HttpStatus.NOT_FOUND, "No habit found for habit ID " + habitId));

            HabitGetDTO habitGetDTO = new HabitGetDTO();
            habitGetDTO.setId(habitId);
            habitGetDTO.setName(habit.getName());
            habitGetDTO.setStreaks(habit.getCurrentStreak());

            // Setting up user check status for all users in the group
            Map<String, Boolean> userCheckStatus = new HashMap<>();
            for (String memberUserId : group.getUserIdList()) {
                // returns bool if memberUser has checked the habit at the current day
                Boolean habitChecked = userStatsEntryService.habitChecked(memberUserId, habitId);
                String userInitials = userService.getInitials(memberUserId);
                userCheckStatus.put(userInitials, habitChecked);
                if (memberUserId.equals(userId)) {
                    habitGetDTO.setChecked(habitChecked);
                }
            }
            habitGetDTO.setUserCheckStatus(userCheckStatus);
            habitGetDTO.setOnSchedule(habit.getRepeatStrategy().repeatsAt(WeekdayUtil.getCurrentWeekday()));
            // add data transfer object to list
            habitDTOs.add(habitGetDTO);
    }
        // finally, send out list
        return ResponseEntity.ok(habitDTOs);
    }

    @GetMapping("/groups/{groupId}/habits/{habitId}")
    public ResponseEntity<?> getHabitData(
            @RequestHeader("Authorization") String authToken,
            @PathVariable String groupId,
            @PathVariable String habitId) {

        boolean isValid = authService.isTokenValid(authToken);
        if (!isValid) {
            return new ResponseEntity<>("Invalid token", HttpStatus.UNAUTHORIZED);
        }

        String userId = authService.getId(authToken);
        Group group = groupService.getGroupById(groupId);
        if (!group.getUserIdList().contains(userId)) {
            return new ResponseEntity<>("User is not part of this group", HttpStatus.UNAUTHORIZED);
        }

        Habit habit = habitService.getHabitById(habitId).orElseThrow(()->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Habit was not found."));
        String name = habit.getName();
        String description = habit.getDescription();
        int currentStreak = habit.getCurrentStreak();
        Map<LocalDate, Map<String, UserStatsStatus>> statusMap = new TreeMap<>();
        for (String memberId : group.getUserIdList()) {
            List<UserStatsEntry> memberEntries = userStatsEntryService.getEntriesByUserIdAndHabitId(memberId, habitId);
            for (UserStatsEntry entry : memberEntries) {
                LocalDate date = entry.getDueDate();
                UserStatsStatus userStatus = entry.getStatus();
                statusMap.computeIfAbsent(date, k -> new HashMap<>())
                        .put(userService.getInitials(memberId), entry.getStatus());
            }
        }

        // Construct DTO
        GroupHabitDataGetDTO groupHabitDataDTO = new GroupHabitDataGetDTO();
        groupHabitDataDTO.setCurrentTeamStreak(currentStreak);
        groupHabitDataDTO.setStatusMap(statusMap);
        groupHabitDataDTO.setName(name);
        groupHabitDataDTO.setDescription(description);

        return ResponseEntity.ok(groupHabitDataDTO);
    }

    @GetMapping("/groups/{groupId}/habits/{habitId}/edit")
    public ResponseEntity<?> getHabitDetails(
            @RequestHeader("Authorization") String authToken,
            @PathVariable String groupId,
            @PathVariable String habitId) {

        boolean isValid = authService.isTokenValid(authToken);
        if (!isValid) {
            return new ResponseEntity<>("Invalid token", HttpStatus.UNAUTHORIZED);
        }

        String userId = authService.getId(authToken);
        Group group = groupService.getGroupById(groupId);
        if (!group.getAdminIdList().contains(userId)) {
            return new ResponseEntity<>("User is not part of this group", HttpStatus.UNAUTHORIZED);
        }
        Habit habit = habitService.getHabitById(habitId).orElseThrow(()->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Habit was not found."));
        return ResponseEntity.ok(habit);
    }

    @PutMapping("/groups/{groupId}/habits/{habitId}/check")
    public ResponseEntity<?> checkHabit(
            @RequestHeader("Authorization") String authToken,
            @PathVariable String groupId,
            @PathVariable String habitId) {

        boolean isValid = authService.isTokenValid(authToken);
        if (!isValid) {
            return new ResponseEntity<>("Invalid token", HttpStatus.UNAUTHORIZED);
        }

        String userId = authService.getId(authToken);
        Group group = groupService.getGroupById(groupId);
        if (!group.getUserIdList().contains(userId)) {
            return new ResponseEntity<>("User is not part of this group", HttpStatus.UNAUTHORIZED);
        }

        Habit habit = habitService.getHabitById(habitId).orElseThrow(()->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Habit was not found."));

        UserStatsStatus statsStatus = userStatsEntryService.checkHabitByUser(habitId,userId);
        String msg = "Habit status changed to " +statsStatus;
        return getHabits(authToken,groupId);
    }

    @PutMapping("/groups/{groupId}/habits/{habitId}/update")
    public ResponseEntity<?> updateHabit(@RequestHeader("Authorization") String authHeaderToken,
                                         @PathVariable String groupId,
                                         @PathVariable String habitId,
                                         @RequestBody String habitData) {
        boolean isValid = authService.isTokenValid(authHeaderToken);
        if (!isValid) {
            return new ResponseEntity<>("Invalid token", HttpStatus.UNAUTHORIZED);
        }

        String userId = authService.getId(authHeaderToken);
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

            // update habit within the db
            habitService.updateHabit(habitId, habit);
            if(!habitService.isCurrentWeekdayActive(habit)){ // checks if the updated habit is not scheduled for the current weekday
                userStatsEntryService.deleteUserStatsEntriesOfToday(habitId);
            }
            else {
                if(!userStatsEntryService.entriesExist(habitId, LocalDate.now())){ // if the habit is scheduled for today but UserStatsEntries exist for today
                    Group group = groupService.getGroupById(groupId);
                    scheduleHabitRoutines(group, habit);
                }
            }
            return new ResponseEntity<>(habit, HttpStatus.CREATED);

        } catch (IOException e) {
            return new ResponseEntity<>("Error parsing JSON", HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/groups/{groupId}/habits/{habitId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ResponseBody
    public ResponseEntity<?> delHabit(
            @RequestHeader("Authorization") String authToken,
            @PathVariable String groupId,
            @PathVariable String habitId) {

        boolean isValid = authService.isTokenValid(authToken);
        if (!isValid) {
            return new ResponseEntity<>("Invalid token", HttpStatus.UNAUTHORIZED);
        }

        String userId = authService.getId(authToken);
        Group group = groupService.getGroupById(groupId);
        if (!group.getAdminIdList().contains(userId)) {
            return new ResponseEntity<>("User is not part of this group", HttpStatus.UNAUTHORIZED);
        }
        groupService.removeHabitFromHabitIdList(groupId, habitId);
        userStatsEntryService.deleteUserStatsEntriesOfToday(habitId);
        habitService.deleteHabit(habitId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }



    public void scheduleHabitRoutines(Group group, Habit habit) {
        Weekday currentWeekday = WeekdayUtil.getCurrentWeekday();
        System.out.println("Today, it is: " +currentWeekday +".");
        LocalDate today = LocalDate.now();
        System.out.println(today);
        // Request group Id
        String groupId = group.getId();
        List<String> groupUserIds = group.getUserIdList();
        List<String> habitIds = group.getHabitIdList();
        // Implement the following steps:
            String habitId = habit.getId();
            RepeatType repeatType = habit.getRepeatStrategy().getRepeatType();
            System.out.println(habitId +" has type " +repeatType);
            if (repeatType.equals(RepeatType.DAILY)){
                // create an user stats entry for each user of this group refered to the current habitId
                for (String userId : groupUserIds){
                    UserStatsEntry userStatsEntry = userStatsEntryService.createUserStatsEntry(userId, groupId,habitId);
                }
            }
            else if (repeatType.equals(RepeatType.WEEKLY)) {
                // ... Check if habit takes place at current weekday
                if(habit.getRepeatStrategy().repeatsAt(currentWeekday)){
                    // create an user stats entry for each user of this group refered to the current habitId
                    for (String userId : groupUserIds){
                        userStatsEntryService.createUserStatsEntry(userId, groupId, habitId);
                    }
                }
            }
        }
}
