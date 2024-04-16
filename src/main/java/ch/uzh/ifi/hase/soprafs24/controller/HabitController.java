package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.constant.RepeatType;
import ch.uzh.ifi.hase.soprafs24.constant.UserStatsStatus;
import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.constant.Weekday;
import ch.uzh.ifi.hase.soprafs24.entity.*;
import ch.uzh.ifi.hase.soprafs24.rest.dto.group.GroupGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.group.GroupHabitDataGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.habit.HabitDateUserStatusGetDTO;
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
import java.time.DayOfWeek;
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

    private final HabitStreakService habitStreakService;

    @Autowired
    public HabitController(HabitService habitService, GroupService groupService, UserService userService, AuthService authService, ObjectMapper objectMapper, UserStatsEntryService userStatsEntryService, HabitStreakService habitStreakService) {
        this.habitService = habitService;
        this.objectMapper = objectMapper;
        this.groupService = groupService;
        this.userService = userService;
        this.authService = authService;
        this.userStatsEntryService = userStatsEntryService;
        this.habitStreakService = habitStreakService;
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
    public ResponseEntity<?> createHabit(@RequestHeader("Authorization") String authToken, @PathVariable String groupId) {
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
            habitGetDTO.setStreaks(habitStreakService.getStreak(habitId, groupId));

            // Setting up user check status for all users in the group
            Map<String, Boolean> userCheckStatus = new HashMap<>();
            for (String memberUserId : group.getUserIdList()) {
                Boolean habitChecked = userStatsEntryService.habitChecked(userId, groupId);
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

        int currentStreak = habitStreakService.getStreak(habitId, groupId);

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

        return ResponseEntity.ok(groupHabitDataDTO);
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
                    System.out.println(userStatsEntry.getDueDate());
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
