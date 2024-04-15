package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.constant.UserStatsStatus;
import ch.uzh.ifi.hase.soprafs24.entity.*;
import ch.uzh.ifi.hase.soprafs24.repository.GroupRepository;
import ch.uzh.ifi.hase.soprafs24.repository.UserStatsEntryRepository;
import ch.uzh.ifi.hase.soprafs24.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;



import ch.uzh.ifi.hase.soprafs24.constant.Weekday;
import ch.uzh.ifi.hase.soprafs24.repository.GroupRepository;
import ch.uzh.ifi.hase.soprafs24.rest.dto.group.GroupGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.group.GroupJoinPostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.group.GroupPostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.group.GroupPutDTO;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs24.service.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.*;


@WebMvcTest(HabitController.class)
public class HabitControllerTest {

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
    private HabitStreakService habitStreakService;

    @MockBean
    private UserScoreService userScoreService;

    @MockBean
    private GroupService groupService;

    @MockBean
    private GroupRepository groupRepository;

    /**
     * ------------------------------ START GET TESTS ------------------------------------------------------------
     */
    @Test //GET Mapping "/groups/{groupId}/habits" - CODE 200 Ok (pass)
    public void GET_GroupHabits_validInput_ReturnsOk() throws Exception {
        String token = "JaZAJ6m4_wh7_ClFK5jr6vvnyRA";
        String groupId = "group1";
        String userId = "user1";

        when(authService.isTokenValid(token)).thenReturn(true);
        when(authService.getId(token)).thenReturn(userId);

        Group group = new Group();
        group.setUserIdList(Arrays.asList(userId));
        group.setHabitIdList(Arrays.asList("IdHabit1", "IdHabit2"));
        when(groupService.getGroupById(groupId)).thenReturn(group);

        Habit habit = new Habit();
        habit.setName("Habit1");
        habit.setRepeatStrategy(new DailyRepeat());
        when(habitService.getHabitById(anyString())).thenReturn(Optional.of(habit));

        when(habitStreakService.getStreak(anyString(), anyString())).thenReturn(1);
        when(userStatsEntryService.habitChecked(anyString(), anyString())).thenReturn(true);
        when(userService.getInitials(anyString())).thenReturn("UI");

        mockMvc.perform(get("/groups/{groupId}/habits", groupId)
                        .header("Authorization", token)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("IdHabit1"))
                .andExpect(jsonPath("$[0].name").value("Habit1"))
                .andExpect(jsonPath("$[0].streaks").value(1))
                .andExpect(jsonPath("$[0].userCheckStatus.UI").value(true));
    }

    @Test //GET Mapping "/groups/{groupId}/habits/{habitId}" - CODE 200 Ok (pass)
    public void GET_GroupHabitData_validInput_ReturnsOk() throws Exception {
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
        habit.setName("Habit1");
        when(habitService.getHabitById(habitId)).thenReturn(Optional.of(habit));

        when(habitStreakService.getStreak(habitId, groupId)).thenReturn(1);

        Map<LocalDate, Map<String, UserStatsStatus>> statusMap = new TreeMap<>();
        LocalDate date = LocalDate.now();
        Map<String, UserStatsStatus> userStatusMap = new HashMap<>();
        userStatusMap.put(userId, UserStatsStatus.SUCCESS);
        statusMap.put(date, userStatusMap);

        UserStatsEntry userStatsEntry = new UserStatsEntry();
        userStatsEntry.setUserId(userId);
        userStatsEntry.setHabitId(habitId);
        userStatsEntry.setDueDate(date);
        userStatsEntry.setStatus(UserStatsStatus.SUCCESS);

        when(userStatsEntryService.getEntriesByUserIdAndHabitId(userId, habitId)).thenReturn(Arrays.asList(userStatsEntry));


        mockMvc.perform(get("/groups/{groupId}/habits/{habitId}", groupId, habitId)
                        .header("Authorization", token)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currentTeamStreak").value(1))
                .andExpect(jsonPath("$.statusMap").isNotEmpty())
                .andExpect(jsonPath("$.name").value("Habit1"));
    }

    /**
     * ------------------------------ END GET TESTS ------------------------------ START POST TESTS ------------------------------
     */
    @Test //POST Mapping "/groups/{groupId}/habits" - CODE 201 CREATED (Pass)
    public void POST_GroupHabits_validInput_ReturnsCreated() throws Exception {
        String token = "JaZAJ6m4_wh7_ClFK5jr6vvnyRA";
        String groupId = "group1";
        String userId = "user1";
        String habitData = "{\"name\":\"Habit1\",\"description\":\"Description1\",\"rewardPoints\":10,\"repeatStrategy\":{\"type\":\"DAILY\"}}";

        when(authService.isTokenValid(token)).thenReturn(true);
        when(authService.getId(token)).thenReturn(userId);

        Group group = new Group();
        group.setUserIdList(Arrays.asList(userId));
        when(groupService.getGroupById(groupId)).thenReturn(group);

        when(groupService.isUserAdmin(userId, groupId)).thenReturn(true);

        Habit habit = new Habit();
        habit.setName("Habit1");
        habit.setDescription("Description1");
        habit.setRewardPoints(10);
        habit.setRepeatStrategy(new DailyRepeat());
        when(habitService.createHabit(any())).thenReturn(habit);

        UserStatsEntry userStatsEntry = new UserStatsEntry();
        userStatsEntry.setDueDate(LocalDate.now());
        userStatsEntry.setUserId(userId);
        userStatsEntry.setGroupId(groupId);
        userStatsEntry.setHabitId(habit.getId());
        String HabitId = userStatsEntry.getHabitId();
        userStatsEntry.setStatus(UserStatsStatus.OPEN);

        when(userStatsEntryService.createUserStatsEntry(userId, groupId, HabitId)).thenReturn(userStatsEntry);

        mockMvc.perform(post("/groups/{groupId}/habits", groupId)
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(habitData))
                .andExpect(status().isCreated());
    }


    /**
     * ------------------------------ END POST TESTS ------------------------------ START PUT TESTS ------------------------------
     */



    /**
     * ------------------------------ END PUT TESTS ------------------------------ START DELETE TESTS ------------------------------
     */



    /**
     * ------------------------------ END DELETE TESTS ------------------------------------------------------------
     */
}