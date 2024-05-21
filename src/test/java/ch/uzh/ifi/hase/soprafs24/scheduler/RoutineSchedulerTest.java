package ch.uzh.ifi.hase.soprafs24.scheduler;

import static org.mockito.Mockito.*;

import ch.uzh.ifi.hase.soprafs24.constant.FeedType;
import ch.uzh.ifi.hase.soprafs24.constant.PulseCheckStatus;
import ch.uzh.ifi.hase.soprafs24.constant.Weekday;
import ch.uzh.ifi.hase.soprafs24.controller.MessageController;
import ch.uzh.ifi.hase.soprafs24.entity.*;
import ch.uzh.ifi.hase.soprafs24.service.*;
import ch.uzh.ifi.hase.soprafs24.util.WeekdayUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

public class RoutineSchedulerTest {

    @Mock
    private GroupService groupService;
    @Mock
    private HabitService habitService;
    @Mock
    private UserStatsEntryService userStatsEntryService;
    @Mock
    private UserScoreService userScoreService;
    @Mock
    private PulseCheckEntryService pulseCheckEntryService;
    @Mock
    private MessageController messageController;
    @Mock
    private FeedMessageService feedMessageService;
    @Mock
    private OpenAIService openAIService;
    @Mock
    private WeekdayUtil weekdayUtil; // Mock WeekdayUtil

    @InjectMocks
    private RoutineScheduler scheduler;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testOpenMorningPulseCheck() throws IOException {
        Group mockGroup = new Group();
        mockGroup.setId("groupId");
        mockGroup.setUserIdList(Arrays.asList("user1", "user2"));
        mockGroup.setName("Test Group"); // Setze den Gruppennamen

        when(groupService.getGroups()).thenReturn(Arrays.asList(mockGroup));
        when(groupService.getGroupById("groupId")).thenReturn(mockGroup); // Mock die Rückgabe der Gruppe
        when(openAIService.generatePulseCheckContentChatCompletion(40, 1)).thenReturn("Generated content");

        scheduler.openMorningPulseCheck();

        verify(pulseCheckEntryService, times(2)).createPulseCheckEntry(any(), eq("groupId"), anyString(), eq("Generated content"), any(LocalDateTime.class), any(LocalDateTime.class), eq(PulseCheckStatus.OPEN));
        verify(messageController, times(1)).sendFeedToGroup(eq("groupId"), any(FeedMessage.class));
    }


    @Test
    public void testCloseMorningPulseCheck() {
        Group mockGroup = new Group();
        mockGroup.setId("groupId");
        mockGroup.setUserIdList(Arrays.asList("user1", "user2"));

        List<PulseCheckEntry> entries = Arrays.asList(
                new PulseCheckEntry("form1", "group1", "user1", "content1", LocalDateTime.now(), LocalDateTime.now(), PulseCheckStatus.ACCEPTED),
                new PulseCheckEntry("form2", "group2", "user2", "content2", LocalDateTime.now(), LocalDateTime.now(), PulseCheckStatus.OPEN)
        );

        when(groupService.getGroups()).thenReturn(Arrays.asList(mockGroup));
        when(groupService.getGroupById("groupId")).thenReturn(mockGroup);
        when(pulseCheckEntryService.findByGroupIdWithLatestEntryDate("groupId")).thenReturn(entries);
        FeedMessage mockFeedMessage = new FeedMessage("formId", "groupId", "groupName", "messageContent", FeedType.PULSECHECK, LocalDateTime.now());
        mockFeedMessage.setId("feedMessageId");
        when(feedMessageService.getLatestPulseCheckMessage(anyString(), anyString())).thenReturn(mockFeedMessage);

        scheduler.closeMorningPulseCheck();

        verify(pulseCheckEntryService, times(1)).setPulseCheckEntryStatus(entries.get(1), PulseCheckStatus.REJECTED);
        verify(feedMessageService, times(2)).addUserSubmit(anyString(), anyString(), anyDouble()); // Expect 2 invocations
    }


    @Test
    public void testEveningRoutineCheck() {
        Group mockGroup = new Group();
        mockGroup.setId("groupId");
        mockGroup.setHabitIdList(Arrays.asList("habit1"));
        mockGroup.setName("Test Group");

        Habit mockHabit = new Habit();
        mockHabit.setName("Running");
        when(groupService.getGroups()).thenReturn(Arrays.asList(mockGroup));
        when(groupService.getGroupById("groupId")).thenReturn(mockGroup); // Mock die Rückgabe der Gruppe
        when(habitService.getHabitById("habit1")).thenReturn(java.util.Optional.of(mockHabit));
        when(userStatsEntryService.allEntriesSuccess("habit1", java.time.LocalDate.now())).thenReturn(true);

        scheduler.checkOpenHabitRoutines();

        verify(messageController, times(1)).sendFeedToGroup(eq("groupId"), any(FeedMessage.class));
    }

    @Test
    public void testMidnightHabitRoutineScheduler() {
        Group mockGroup = new Group();
        mockGroup.setId("groupId");
        mockGroup.setHabitIdList(Arrays.asList("habit1"));
        mockGroup.setUserIdList(Arrays.asList("user1", "user2"));
        mockGroup.setName("Test Group");

        Habit mockHabit = new Habit();
        mockHabit.setName("Yoga");
        mockHabit.setRepeatStrategy(new DailyRepeat());

        when(groupService.getGroups()).thenReturn(Arrays.asList(mockGroup));
        when(habitService.getHabitById("habit1")).thenReturn(java.util.Optional.of(mockHabit));
        when(groupService.getGroupById("groupId")).thenReturn(mockGroup); // Make sure this is mocked if used in the method

        try (MockedStatic<WeekdayUtil> weekdayUtil = Mockito.mockStatic(WeekdayUtil.class)) {
            weekdayUtil.when(WeekdayUtil::getCurrentWeekday).thenReturn(Weekday.MONDAY);

            scheduler.checkAndScheduleHabitRoutines();

            verify(userStatsEntryService, times(2)).createUserStatsEntry(anyString(), eq("groupId"), eq("habit1"));
        }
    }
}
