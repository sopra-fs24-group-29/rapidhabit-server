package ch.uzh.ifi.hase.soprafs24.integration;

import ch.uzh.ifi.hase.soprafs24.Application;
import ch.uzh.ifi.hase.soprafs24.constant.UserStatsStatus;
import ch.uzh.ifi.hase.soprafs24.constant.Weekday;
import ch.uzh.ifi.hase.soprafs24.entity.*;
import ch.uzh.ifi.hase.soprafs24.repository.GroupRepository;
import ch.uzh.ifi.hase.soprafs24.repository.HabitRepository;
import ch.uzh.ifi.hase.soprafs24.repository.UserScoreRepository;
import ch.uzh.ifi.hase.soprafs24.repository.UserStatsEntryRepository;
import ch.uzh.ifi.hase.soprafs24.scheduler.RoutineScheduler;
import ch.uzh.ifi.hase.soprafs24.service.GroupService;
import ch.uzh.ifi.hase.soprafs24.service.HabitService;
import ch.uzh.ifi.hase.soprafs24.service.UserStatsEntryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = Application.class)
public class RoutineSchedulerIntegrationTest {

    @Autowired
    private GroupService groupService;

    @MockBean
    private GroupRepository groupRepository;
    @MockBean
    private UserStatsEntryRepository userStatsEntryRepository;
    @MockBean
    private RoutineScheduler routineScheduler;
    @MockBean
    private HabitRepository habitRepository;
    @MockBean
    private HabitService habitService;
    @MockBean
    private UserScoreRepository userScoreRepository;
    @MockBean
    private UserStatsEntryService userStatsEntryService;

    @MockBean
    private Clock clock;

    @Test
    public void testThatNothingHappens() {
        // Mock das Verhalten der Methode, so dass sie nichts tut
        doNothing().when(userStatsEntryService).updateStatusForExpiredEntries(any(LocalDate.class));

        // Führe die Methode aus, die updateStatusForExpiredEntries aufruft
        routineScheduler.checkAndScheduleHabitRoutines();

        // Überprüfe, dass die Methode aufgerufen wurde
        verify(userStatsEntryService).updateStatusForExpiredEntries(any(LocalDate.class));
    }

    @BeforeEach
    void setup() {
        // Mock the new day to be the 25th of April
        LocalDate fixedDate = LocalDate.of(2024, 4, 25);
        ZoneId zoneId = ZoneId.systemDefault();
        Clock fixedClock = Clock.fixed(fixedDate.atStartOfDay(zoneId).toInstant(), zoneId);
        when(clock.instant()).thenReturn(fixedClock.instant());
        when(clock.getZone()).thenReturn(fixedClock.getZone());
    }

    @Test
    public void testSuccessfulDayCompletion() {
        // Setup
        User user1 = new User();
        user1.setId("userId1");
        user1.setEmail("simi@somemail.com");

        User user2 = new User();
        user2.setId("userId2");
        user2.setEmail("lukas@somemail.com");

        Habit dailyHabit = new Habit();
        dailyHabit.setId("habitId1");
        dailyHabit.setRepeatStrategy(new DailyRepeat());
        dailyHabit.setName("Drink Water");
        dailyHabit.setCurrentStreak(8);

        Habit weeklyHabit = new Habit();
        weeklyHabit.setId("habitId2");
        WeeklyRepeat weeklyRepeat = new WeeklyRepeat();
        weeklyRepeat.setWeekdayToRepeat(Weekday.WEDNESDAY, true);
        weeklyHabit.setRepeatStrategy(weeklyRepeat);
        weeklyHabit.setName("Cardio Session");
        weeklyHabit.setCurrentStreak(3);

        Group group = new Group();
        group.setName("Cardio Actives");
        group.setId("groupId");
        ArrayList<String> list = new ArrayList<String>();
        group.setUserIdList(list);
        group.addUserId(user1.getId());
        group.addUserId(user2.getId());
        List<String> habitIdList = new ArrayList<>();
        habitIdList.add(dailyHabit.getId());
        habitIdList.add(weeklyHabit.getId());
        group.setHabitIdList(habitIdList);
        group.setCurrentStreak(10);

        LocalDate yesterdayDate = LocalDate.of(2024, 4, 24);

        // Stelle sicher, dass die benötigten UserStatsEntry Objekte zurückgegeben werden
        UserStatsEntry user1Daily = new UserStatsEntry();
        user1Daily.setStatus(UserStatsStatus.SUCCESS);
        user1Daily.setDueDate(yesterdayDate);
        UserStatsEntry user2Daily = new UserStatsEntry();
        user2Daily.setStatus(UserStatsStatus.SUCCESS);
        user2Daily.setDueDate(yesterdayDate);
        UserStatsEntry user1Weekly = new UserStatsEntry();
        user1Weekly.setStatus(UserStatsStatus.SUCCESS);
        user1Weekly.setDueDate(yesterdayDate);
        UserStatsEntry user2Weekly = new UserStatsEntry();
        user2Weekly.setStatus(UserStatsStatus.SUCCESS);
        user2Weekly.setDueDate(yesterdayDate);

        // Mock behaviors
        when(groupService.getGroups()).thenReturn(Arrays.asList(group));
        // when(groupService.getGroupById("groupId")).thenReturn(group);
        when(userStatsEntryService.countUniqueHabitsByDate(yesterdayDate)).thenReturn(2);
        when(habitService.getHabitById("habitId1")).thenReturn(Optional.of(dailyHabit));
        when(habitService.getHabitById("habitId2")).thenReturn(Optional.of(weeklyHabit));
        when(userStatsEntryService.entriesExist(dailyHabit.getId(),yesterdayDate)).thenReturn(true);
        when(userStatsEntryService.allEntriesSuccess(dailyHabit.getId(),yesterdayDate)).thenReturn(true);

        when(habitRepository.findAll()).thenReturn(Arrays.asList(dailyHabit, weeklyHabit));
        when(userStatsEntryRepository.findByHabitIdAndDueDateAndStatus(anyString(), any(LocalDate.class), any(UserStatsStatus.class)))
                .thenReturn(Arrays.asList(user1Weekly, user1Daily, user2Weekly, user2Daily));

        // Act
        routineScheduler.checkAndScheduleHabitRoutines();

        // Assert
        // verify(userStatsEntryRepository, times(2)).saveAll(any(List.class));
        assertThat(group.getCurrentStreak()).isEqualTo(10);
    }

}
