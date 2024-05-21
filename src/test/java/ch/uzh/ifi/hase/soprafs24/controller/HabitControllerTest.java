package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.constant.UserStatsStatus;
import ch.uzh.ifi.hase.soprafs24.constant.Weekday;
import ch.uzh.ifi.hase.soprafs24.entity.*;
import ch.uzh.ifi.hase.soprafs24.repository.GroupRepository;
import ch.uzh.ifi.hase.soprafs24.rest.dto.habit.HabitGetDTO;
import ch.uzh.ifi.hase.soprafs24.service.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.web.server.ResponseStatusException;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;


@WebMvcTest(HabitController.class)
class HabitControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @MockBean
    private UserService userService;

    @MockBean
    private UserStatsEntryService userStatsEntryService;

    @MockBean
    private HabitService habitService;

    @MockBean
    private UserScoreService userScoreService;

    @MockBean
    private GroupService groupService;

    @MockBean
    private GroupRepository groupRepository;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * ------------------------------ START GET TESTS ------------------------------------------------------------
     */
    @Test //GET Mapping "/groups/{groupId}/habits" - CODE 200 Ok (pass)
    void GET_GroupHabits_validInput_ReturnsOk() throws Exception {
        String token = "JaZAJ6m4_wh7_ClFK5jr6vvnyRA";
        String groupId = "group1";
        String userId = "user1";

        when(authService.isTokenValid(token)).thenReturn(true);
        when(authService.getId(token)).thenReturn(userId);

        Group group = new Group();
        group.setUserIdList(Arrays.asList(userId));
        group.setHabitIdList(Arrays.asList("IdHabit1", "IdHabit2"));
        when(groupService.getGroupById(groupId)).thenReturn(group);

        Habit habit1 = new Habit();
        habit1.setId("IdHabit1");
        habit1.setName("Habit1");
        habit1.setCurrentStreak(1);
        habit1.setRepeatStrategy(new DailyRepeat());
        when(habitService.getHabitById("IdHabit1")).thenReturn(Optional.of(habit1));

        Habit habit2 = new Habit();
        habit2.setId("IdHabit2");
        habit2.setName("Habit2");
        habit2.setCurrentStreak(2);
        habit2.setRepeatStrategy(new WeeklyRepeat());
        when(habitService.getHabitById("IdHabit2")).thenReturn(Optional.of(habit2));

        when(userStatsEntryService.habitChecked(anyString(), anyString())).thenReturn(true);
        when(userService.getInitials(anyString())).thenReturn("UI");

        mockMvc.perform(get("/groups/{groupId}/habits", groupId)
                        .header("Authorization", token)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("IdHabit1"))
                .andExpect(jsonPath("$[0].name").value("Habit1"))
                .andExpect(jsonPath("$[0].streaks").value(1))
                .andExpect(jsonPath("$[0].userCheckStatus.UI").value(true))
                .andExpect(jsonPath("$[1].id").value("IdHabit2"))
                .andExpect(jsonPath("$[1].name").value("Habit2"))
                .andExpect(jsonPath("$[1].streaks").value(2))
                .andExpect(jsonPath("$[1].userCheckStatus.UI").value(true));
    }

    @Test
    void GET_getHabits_invalidToken_ReturnsUnauthorized() throws Exception {
        String invalidAuthToken = "invalidToken";
        String groupId = "group1";

        when(authService.isTokenValid(invalidAuthToken)).thenReturn(false);

        mockMvc.perform(get("/groups/{groupId}/habits", groupId)
                        .header("Authorization", invalidAuthToken))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Invalid token"));
    }

    @Test
    void GET_getHabits_userNotPartOfGroup_ReturnsUnauthorized() throws Exception {
        String token = "validToken";
        String groupId = "group1";
        String userId = "user2";
        when(authService.isTokenValid(token)).thenReturn(true);
        when(authService.getId(token)).thenReturn(userId);

        Group group = new Group();
        group.setUserIdList(Collections.singletonList("user1")); // Only "user1" is in the userIdList

        when(groupService.getGroupById(groupId)).thenReturn(group);

        mockMvc.perform(get("/groups/{groupId}/habits", groupId)
                        .header("Authorization", token))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("User is not part of this group"));
    }



    @Test //GET Mapping "/groups/{groupId}/habits/{habitId}" - CODE 200 Ok (pass)
    void GET_GroupHabitData_validInput_ReturnsOk() throws Exception {
        String token = "JaZAJ6m4_wh7_ClFK5jr6vvnyRA";
        String groupId = "group1";
        String habitId = "habit1";
        String userId = "user1";

        when(authService.isTokenValid(token)).thenReturn(true);
        when(authService.getId(token)).thenReturn(userId);

        Group group = new Group();
        group.setUserIdList(Arrays.asList(userId));
        when(groupService.getGroupById(groupId)).thenReturn(group);

        Habit habit = new Habit();
        habit.setId(habitId);
        habit.setName("Habit1");
        habit.setCurrentStreak(1);
        when(habitService.getHabitById(habitId)).thenReturn(Optional.of(habit));

        LocalDate date = LocalDate.now();
        UserStatsEntry userStatsEntry = new UserStatsEntry();
        userStatsEntry.setUserId(userId);
        userStatsEntry.setHabitId(habitId);
        userStatsEntry.setDueDate(date);
        userStatsEntry.setStatus(UserStatsStatus.SUCCESS);

        when(userStatsEntryService.getEntriesByUserIdAndHabitId(userId, habitId)).thenReturn(Arrays.asList(userStatsEntry));
        when(userService.getInitials(userId)).thenReturn("UI");


        mockMvc.perform(get("/groups/{groupId}/habits/{habitId}", groupId, habitId)
                        .header("Authorization", token)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currentTeamStreak").value(1))
                .andExpect(jsonPath("$.statusMap").isNotEmpty())
                .andExpect(jsonPath("$.statusMap." + date.toString() + ".UI").value("SUCCESS"))
                .andExpect(jsonPath("$.name").value("Habit1"));
    }

    @Test //GET Mapping "/groups/{groupId}/habits/{habitId}" - CODE 404 Not Found (Error)
    void GET_GroupHabitData_InvalidInput_NotFound() throws Exception {
        String token = "JaZAJ6m4_wh7_ClFK5jr6vvnyRA";
        String groupId = "group1";
        String habitId = "habit1";
        String userId = "user1";

        when(authService.isTokenValid(token)).thenReturn(true);
        when(authService.getId(token)).thenReturn(userId);

        Group group = new Group();
        group.setUserIdList(Arrays.asList(userId));
        when(groupService.getGroupById(groupId)).thenReturn(group);

        when(habitService.getHabitById(habitId)).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "No group with id " + groupId + " found."));


        mockMvc.perform(get("/groups/{groupId}/habits/{habitId}", groupId, habitId)
                        .header("Authorization", token)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
    @Test //GET Mapping "/groups/{groupId}/habits/{habitId}" - CODE 404 Not Found (Error)
    void GET_GroupHabitData_validInput_InvalidToken() throws Exception {
        String token = "JaZAJ6m4_wh7_ClFK5jr6vvnyRA";
        String groupId = "group1";
        String habitId = "habit1";
        String userId = "user1";

        when(authService.isTokenValid(token)).thenReturn(false);

        mockMvc.perform(get("/groups/{groupId}/habits/{habitId}", groupId, habitId)
                        .header("Authorization", token)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test //GET Mapping "/groups/{groupId}/habits/{habitId}/edit" - CODE 200 Ok (pass)
    void GET_GroupHabitData_ValidInput_Success() throws Exception {
        String token = "JaZAJ6m4_wh7_ClFK5jr6vvnyRA";
        when(authService.isTokenValid(token)).thenReturn(true);
        Long userId = 1L;
        String groupId = "1";
        String habitId = "habit1";
        when(authService.getId(token)).thenReturn(String.valueOf(userId));
        when(groupService.isUserAdmin(String.valueOf(userId), groupId)).thenReturn(true);

        Group group = new Group();
        group.setAdminIdList(Arrays.asList(String.valueOf(userId)));
        when(groupService.getGroupById(groupId)).thenReturn(group);

        Habit habit = new Habit();
        habit.setId(habitId);
        when(habitService.getHabitById(habitId)).thenReturn(Optional.of(habit));

        mockMvc.perform(get("/groups/{groupId}/habits/{habitId}/edit", groupId, habitId)
                        .header("Authorization", token)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    /**
     * ------------------------------ END GET TESTS ------------------------------ START POST TESTS ------------------------------
     */
    @Test //POST Mapping "/groups/{groupId}/habits" - CODE 201 CREATED (Pass)
    void POST_Create_GroupHabits_daily_validInput_ReturnsCreated() throws Exception {
        String token = "JaZAJ6m4_wh7_ClFK5jr6vvnyRA";
        String groupId = "group1";
        String userId = "user1";
        String habitData = "{\"name\":\"Habit1\",\"description\":\"Description1\",\"rewardPoints\":10,\"repeatStrategy\":{\"type\":\"DAILY\"}}";

        when(authService.isTokenValid(token)).thenReturn(true);
        when(authService.getId(token)).thenReturn(userId);

        Group group = new Group();
        group.setId(groupId);
        group.setUserIdList(Arrays.asList(userId));
        when(groupService.getGroupById(groupId)).thenReturn(group);
        when(groupService.isUserAdmin(userId, groupId)).thenReturn(true);

        Habit habit = new Habit();
        habit.setName("Habit1");
        habit.setDescription("Description1");
        habit.setRewardPoints(10);
        habit.setRepeatStrategy(new DailyRepeat());
        when(habitService.createHabit(any(Habit.class))).thenAnswer(invocation -> {
            Habit habitToCreate = invocation.getArgument(0);
            habitToCreate.setId("habitId1"); // Simulate setting the ID of the created habit
            return habitToCreate;
        });

        mockMvc.perform(post("/groups/{groupId}/habits", groupId)
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(habitData))
                .andExpect(status().isCreated());
    }

    @Test
    void POST_Create_GroupHabits_WeeklyRepeat_ValidInput_ReturnsCreated() throws Exception {
        String token = "JaZAJ6m4_wh7_ClFK5jr6vvnyRA";
        String groupId = "group1";
        String userId = "user1";
        // JSON payload with a WEEKLY repeat strategy and a weekdayMap
        String habitData = "{\n" +
                "    \"name\": \"Habit1\",\n" +
                "    \"description\": \"Description1\",\n" +
                "    \"rewardPoints\": 10,\n" +
                "    \"repeatStrategy\": {\n" +
                "        \"type\": \"WEEKLY\",\n" +
                "        \"weekdayMap\": {\n" +
                "            \"MONDAY\": true,\n" +
                "            \"WEDNESDAY\": false,\n" +
                "            \"FRIDAY\": true\n" +
                "        }\n" +
                "    }\n" +
                "}";

        when(authService.isTokenValid(token)).thenReturn(true);
        when(authService.getId(token)).thenReturn(userId);

        Group group = new Group();
        group.setId(groupId);
        group.setUserIdList(Arrays.asList(userId));
        when(groupService.getGroupById(groupId)).thenReturn(group);
        when(groupService.isUserAdmin(userId, groupId)).thenReturn(true);

        Habit habit = new Habit();
        habit.setName("Habit1");
        habit.setDescription("Description1");
        habit.setRewardPoints(10);
        // Assuming WeeklyRepeat and DailyRepeat are subclasses of a common RepeatStrategy interface
        WeeklyRepeat weeklyRepeat = new WeeklyRepeat();
        weeklyRepeat.setWeekdayToRepeat(Weekday.MONDAY, true);
        weeklyRepeat.setWeekdayToRepeat(Weekday.WEDNESDAY, false);
        weeklyRepeat.setWeekdayToRepeat(Weekday.FRIDAY, true);
        habit.setRepeatStrategy(weeklyRepeat);
        when(habitService.createHabit(any(Habit.class))).thenAnswer(invocation -> {
            Habit habitToCreate = invocation.getArgument(0);
            habitToCreate.setId("habitId1"); // Simulate setting the ID of the created habit
            return habitToCreate;
        });

        mockMvc.perform(post("/groups/{groupId}/habits", groupId)
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(habitData))
                .andExpect(status().isCreated());
    }


    @Test //POST Mapping "/groups/{groupId}/habits" - CODE 401 Unauthorized (Error)
    void POST_Create_GroupHabits_InvalidInput() throws Exception {
        String token = "JaZAJ6m4_wh7_ClFK5jr6vvnyRA";
        String groupId = "group1";
        String userId = "user1";
        String habitData = "{\"name\":\"Habit1\",\"description\":\"Description1\",\"rewardPoints\":10,\"repeatStrategy\":{\"type\":\"DAILY\"}}";

        when(authService.isTokenValid(token)).thenReturn(true);
        when(authService.getId(token)).thenReturn(userId);

        Group group = new Group();
        group.setId(groupId);
        group.setUserIdList(Arrays.asList(userId));
        when(groupService.getGroupById(groupId)).thenReturn(group);

        when(groupService.isUserAdmin(userId, groupId)).thenReturn(false);

        mockMvc.perform(post("/groups/{groupId}/habits", groupId)
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(habitData))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void POST_createHabit_invalidToken_ReturnsUnauthorized() throws Exception {
        String invalidAuthHeaderToken = "invalidToken";
        String groupId = "group1";
        String habitData = "{\"name\":\"Test Habit\",\"description\":\"A test habit\",\"rewardPoints\":10,\"repeatStrategy\":{\"type\":\"DAILY\"}}";

        when(authService.isTokenValid(invalidAuthHeaderToken)).thenReturn(false);

        mockMvc.perform(post("/groups/{groupId}/habits", groupId)
                        .header("Authorization", invalidAuthHeaderToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(habitData))
                .andExpect(status().isUnauthorized());
    }


    /**
     * ------------------------------ END POST TESTS ------------------------------ START PUT TESTS ------------------------------
     */

    @Test //PUT Mapping "/groups/{groupId}/habits/{habitId}/check" - CODE 200 OK (Success)
    void PUT_Check_GroupHabits_ValidInput_ReturnsOk() throws Exception {
        String token = "JaZAJ6m4_wh7_ClFK5jr6vvnyRA";
        String groupId = "group1";
        String userId = "user1";
        String habitId = "habitId1";

        when(authService.isTokenValid(token)).thenReturn(true);
        when(authService.getId(token)).thenReturn(userId);

        Group group = new Group();
        group.setId(groupId);
        group.setUserIdList(Arrays.asList(userId));
        group.setHabitIdList(Arrays.asList("IdHabit1", "IdHabit2"));
        when(groupService.getGroupById(groupId)).thenReturn(group);

        Habit habit1 = new Habit();
        habit1.setId("IdHabit1");
        habit1.setName("Habit1");
        habit1.setCurrentStreak(1);
        habit1.setRepeatStrategy(new DailyRepeat());
        when(habitService.getHabitById("IdHabit1")).thenReturn(Optional.of(habit1));

        Habit habit2 = new Habit();
        habit2.setId("IdHabit2");
        habit2.setName("Habit2");
        habit2.setCurrentStreak(2);
        habit2.setRepeatStrategy(new WeeklyRepeat());
        when(habitService.getHabitById("IdHabit2")).thenReturn(Optional.of(habit2));

        Habit habit = new Habit();
        habit.setId(habitId);
        when(habitService.getHabitById(habitId)).thenReturn(Optional.of(habit));

        when(userStatsEntryService.checkHabitByUser(habitId, userId)).thenReturn(UserStatsStatus.SUCCESS);

        when(userStatsEntryService.habitChecked(anyString(), anyString())).thenReturn(true);
        when(userService.getInitials(anyString())).thenReturn("UI");

        mockMvc.perform(put("/groups/{groupId}/habits/{habitId}/check", groupId, habitId)
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("IdHabit1"))
                .andExpect(jsonPath("$[0].name").value("Habit1"))
                .andExpect(jsonPath("$[0].streaks").value(1))
                .andExpect(jsonPath("$[0].userCheckStatus.UI").value(true))
                .andExpect(jsonPath("$[1].id").value("IdHabit2"))
                .andExpect(jsonPath("$[1].name").value("Habit2"))
                .andExpect(jsonPath("$[1].streaks").value(2))
                .andExpect(jsonPath("$[1].userCheckStatus.UI").value(true));
    }

    @Test
    void PUT_Update_GroupHabits_ValidInput_ReturnsCreated() throws Exception {
        String token = "validToken";
        String groupId = "group1";
        String habitId = "habit1";
        String habitData = "{\"name\":\"Habit1\",\"description\":\"Description1\",\"rewardPoints\":10,\"repeatStrategy\":{\"type\":\"DAILY\"}}";
        String userId = "user1";

        when(authService.isTokenValid(token)).thenReturn(true);
        when(authService.getId(token)).thenReturn(userId);
        when(groupService.isUserAdmin(userId, groupId)).thenReturn(true);

        Habit habit = new Habit();
        habit.setName("Habit1");
        habit.setDescription("Description1");
        habit.setRewardPoints(10);
        habit.setRepeatStrategy(new DailyRepeat());
        when(habitService.updateHabit(eq(habitId), any(Habit.class))).thenReturn(habit);

        mockMvc.perform(put("/groups/{groupId}/habits/{habitId}/update", groupId, habitId)
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(habitData))
                .andExpect(status().isCreated());
    }

    @Test
    void PUT_Update_GroupHabits_InvalidWeekdayName_ReturnsBadRequest() throws Exception {
        String token = "validToken";
        String groupId = "group1";
        String habitId = "habit1";
        String habitData = "{\"name\":\"Habit1\",\"description\":\"Description1\",\"rewardPoints\":10,\"repeatStrategy\":{\"type\":\"WEEKLY\",\"weekdayMap\":{\"invalidDay\":true}}}";
        String userId = "user1";

        when(authService.isTokenValid(token)).thenReturn(true);
        when(authService.getId(token)).thenReturn(userId);
        when(groupService.isUserAdmin(userId, groupId)).thenReturn(true);

        mockMvc.perform(put("/groups/{groupId}/habits/{habitId}/update", groupId, habitId)
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(habitData))
                .andExpect(status().isBadRequest());
    }

    @Test
    void PUT_Update_GroupHabits_UserNotAdmin_ReturnsUnauthorized() throws Exception {
        String token = "validToken";
        String groupId = "group1";
        String habitId = "habit1";
        String habitData = "{\"name\":\"Habit1\",\"description\":\"Description1\",\"rewardPoints\":10,\"repeatStrategy\":{\"type\":\"DAILY\"}}";
        String userId = "user1";

        when(authService.isTokenValid(token)).thenReturn(true);
        when(authService.getId(token)).thenReturn(userId);
        when(groupService.isUserAdmin(userId, groupId)).thenReturn(false);

        mockMvc.perform(put("/groups/{groupId}/habits/{habitId}/update", groupId, habitId)
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(habitData))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void PUT_Update_GroupHabits_InvalidToken_ReturnsUnauthorized() throws Exception {
        String token = "invalidToken";
        String groupId = "group1";
        String habitId = "habit1";
        String habitData = "{\"name\":\"Habit1\",\"description\":\"Description1\",\"rewardPoints\":10,\"repeatStrategy\":{\"type\":\"DAILY\"}}";

        when(authService.isTokenValid(token)).thenReturn(false);

        mockMvc.perform(put("/groups/{groupId}/habits/{habitId}/update", groupId, habitId)
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(habitData))
                .andExpect(status().isUnauthorized());
    }


    /**
     * ------------------------------ END PUT TESTS ------------------------------ START DELETE TESTS ------------------------------
     */

    @Test //DELETE Mapping "/groups/{groupId}/habits/{habitId}" - CODE 204 No Content (Success)
    void DELETE_GroupHabit_ValidInput_Success() throws Exception {
        String token = "JaZAJ6m4_wh7_ClFK5jr6vvnyRA";
        when(authService.isTokenValid(token)).thenReturn(true);
        Long userId = 1L;
        String groupId = "1";
        String habitId = "habit1";
        when(authService.getId(token)).thenReturn(String.valueOf(userId));
        when(groupService.isUserAdmin(String.valueOf(userId), groupId)).thenReturn(true);

        Group group = new Group();
        group.setAdminIdList(Arrays.asList(String.valueOf(userId)));
        when(groupService.getGroupById(groupId)).thenReturn(group);

        doNothing().when(habitService).deleteHabit(habitId);

        mockMvc.perform(delete("/groups/{groupId}/habits/{habitId}", groupId, habitId)
                        .header("Authorization", token)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }


    /**
     * ------------------------------ END DELETE TESTS ------------------------------------------------------------
     */
}