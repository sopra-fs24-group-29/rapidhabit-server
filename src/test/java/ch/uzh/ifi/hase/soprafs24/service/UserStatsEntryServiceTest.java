package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.constant.UserStatsStatus;
import ch.uzh.ifi.hase.soprafs24.entity.Habit;
import ch.uzh.ifi.hase.soprafs24.entity.UserStatsEntry;
import ch.uzh.ifi.hase.soprafs24.repository.HabitRepository;
import ch.uzh.ifi.hase.soprafs24.repository.UserStatsEntryRepository;
import ch.uzh.ifi.hase.soprafs24.rest.dto.habit.HabitDateUserStatusGetDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class UserStatsEntryServiceTest {
    @Mock
    HabitRepository habitRepository;
    @Mock
    UserStatsEntryRepository userStatsEntryRepository;
    @InjectMocks
    private UserStatsEntryService userStatsEntryService;
    @Test
    void createUserStatsEntry_validInput_success() {
        UserStatsEntry testEntry = new UserStatsEntry();
        testEntry.setUserId("1");
        testEntry.setGroupId("2");
        testEntry.setHabitId("3");
        when(userStatsEntryRepository.save(any(UserStatsEntry.class))).thenReturn(testEntry);

        UserStatsEntry result = userStatsEntryService.createUserStatsEntry("1", "2", "3");

        assertEquals(testEntry.getUserId(), result.getUserId());
        assertEquals(testEntry.getGroupId(), result.getGroupId());
        assertEquals(testEntry.getHabitId(), result.getHabitId());
        verify(userStatsEntryRepository, times(1)).save(any(UserStatsEntry.class));
    }
    @Test
    void findAll_success() {
        UserStatsEntry testEntry1 = new UserStatsEntry();
        testEntry1.setId("1");
        UserStatsEntry testEntry2 = new UserStatsEntry();
        testEntry2.setId("2");
        List<UserStatsEntry> entryList = Arrays.asList(testEntry1, testEntry2);
        when(userStatsEntryRepository.findAll()).thenReturn(entryList);

        List<UserStatsEntry> result = userStatsEntryService.findAll();

        assertEquals(2, result.size());
        assertEquals(testEntry1.getId(), result.get(0).getId());
        assertEquals(testEntry2.getId(), result.get(1).getId());
    }
    @Test
    void findById_success() {
        UserStatsEntry testEntry = new UserStatsEntry();
        testEntry.setId("1");
        when(userStatsEntryRepository.findById("1")).thenReturn(Optional.of(testEntry));

        Optional<UserStatsEntry> result = userStatsEntryService.findById("1");

        assertTrue(result.isPresent());
        assertEquals(testEntry.getId(), result.get().getId());
    }
    @Test
    void save_success() {
        UserStatsEntry testEntry = new UserStatsEntry();
        testEntry.setId("1");
        when(userStatsEntryRepository.save(any(UserStatsEntry.class))).thenReturn(testEntry);

        UserStatsEntry result = userStatsEntryService.save(testEntry);

        assertEquals(testEntry.getId(), result.getId());
        verify(userStatsEntryRepository, times(1)).save(any(UserStatsEntry.class));
    }
    @Test
    void deleteById_success() {
        doNothing().when(userStatsEntryRepository).deleteById("1");

        userStatsEntryService.deleteById("1");

        verify(userStatsEntryRepository, times(1)).deleteById("1");
    }
    @Test
    void findEntriesBeforeDate_success() {
        UserStatsEntry testEntry1 = new UserStatsEntry();
        testEntry1.setId("1");
        UserStatsEntry testEntry2 = new UserStatsEntry();
        testEntry2.setId("2");
        List<UserStatsEntry> entryList = Arrays.asList(testEntry1, testEntry2);
        LocalDate inputDate = LocalDate.now();
        when(userStatsEntryRepository.findAllByDueDateLessThan(inputDate.minusDays(1))).thenReturn(entryList);

        List<UserStatsEntry> result = userStatsEntryService.findEntriesBeforeDate(inputDate);

        assertEquals(2, result.size());
        assertEquals(testEntry1.getId(), result.get(0).getId());
        assertEquals(testEntry2.getId(), result.get(1).getId());
    }
    @Test
    void updateStatusForExpiredEntries_success() {
        UserStatsEntry testEntry1 = new UserStatsEntry();
        testEntry1.setId("1");
        testEntry1.setStatus(UserStatsStatus.OPEN);
        UserStatsEntry testEntry2 = new UserStatsEntry();
        testEntry2.setId("2");
        testEntry2.setStatus(UserStatsStatus.OPEN);
        List<UserStatsEntry> entryList = Arrays.asList(testEntry1, testEntry2);
        LocalDate inputDate = LocalDate.now();
        when(userStatsEntryRepository.findAllByDueDateLessThanAndStatus(inputDate, UserStatsStatus.OPEN)).thenReturn(entryList);

        userStatsEntryService.updateStatusForExpiredEntries(inputDate);

        assertEquals(UserStatsStatus.FAIL, testEntry1.getStatus());
        assertEquals(UserStatsStatus.FAIL, testEntry2.getStatus());
        verify(userStatsEntryRepository, times(1)).saveAll(entryList);
    }
    @Test
    void habitChecked_success() {
        UserStatsEntry testEntry = new UserStatsEntry();
        testEntry.setStatus(UserStatsStatus.SUCCESS);
        when(userStatsEntryRepository.findByUserIdAndHabitIdAndDueDate("1", "2", LocalDate.now())).thenReturn(Optional.of(testEntry));

        Boolean result = userStatsEntryService.habitChecked("1", "2");

        assertTrue(result);
    }
    @Test
    void computeUserRanks_success() {
        UserStatsEntry testEntry1 = new UserStatsEntry();
        testEntry1.setUserId("1");
        testEntry1.setStatus(UserStatsStatus.SUCCESS);
        testEntry1.setHabitId("3");
        Habit testHabit = new Habit();
        testHabit.setRewardPoints(10);
        when(userStatsEntryRepository.findAllByGroupId("2")).thenReturn(Arrays.asList(testEntry1));
        when(habitRepository.findById("3")).thenReturn(Optional.of(testHabit));

        Map<String, Integer> result = userStatsEntryService.computeUserRanks("2");

        assertEquals(1, result.size());
        assertEquals(10, result.get("1").intValue());
    }

    @Test
    void getHabitData_success() {
        UserStatsEntry testEntry1 = new UserStatsEntry();
        testEntry1.setDueDate(LocalDate.now());
        testEntry1.setUserId("1");
        testEntry1.setStatus(UserStatsStatus.SUCCESS);
        when(userStatsEntryRepository.findByHabitIdAndGroupId("3", "2")).thenReturn(Arrays.asList(testEntry1));

        List<HabitDateUserStatusGetDTO> result = userStatsEntryService.getHabitData("3", "2");

        assertEquals(1, result.size());
        assertEquals(testEntry1.getDueDate(), result.get(0).getDate());
        assertEquals(testEntry1.getUserId(), result.get(0).getUserId());
        assertEquals(testEntry1.getStatus(), result.get(0).getStatus());
    }

    @Test
    void getEntriesByUserIdAndHabitId_success() {
        UserStatsEntry testEntry1 = new UserStatsEntry();
        testEntry1.setId("1");
        when(userStatsEntryRepository.findByUserIdAndHabitId("1", "2")).thenReturn(Arrays.asList(testEntry1));

        List<UserStatsEntry> result = userStatsEntryService.getEntriesByUserIdAndHabitId("1", "2");

        assertEquals(1, result.size());
        assertEquals(testEntry1.getId(), result.get(0).getId());
    }
    @Test
    void checkHabitByUser_openToSuccess() {
        UserStatsEntry testEntry = new UserStatsEntry();
        testEntry.setStatus(UserStatsStatus.OPEN);
        when(userStatsEntryRepository.findByUserIdAndHabitIdAndDueDate("1", "2", LocalDate.now())).thenReturn(Optional.of(testEntry));
        when(userStatsEntryRepository.save(any(UserStatsEntry.class))).thenReturn(testEntry);

        UserStatsStatus result = userStatsEntryService.checkHabitByUser("2", "1");

        assertEquals(UserStatsStatus.SUCCESS, result);
        assertEquals(UserStatsStatus.SUCCESS, testEntry.getStatus());
    }
    @Test
    void checkHabitByUser_successToOpen() {
        UserStatsEntry testEntry = new UserStatsEntry();
        testEntry.setStatus(UserStatsStatus.SUCCESS); // Initial status is SUCCESS
        when(userStatsEntryRepository.findByUserIdAndHabitIdAndDueDate("1", "2", LocalDate.now())).thenReturn(Optional.of(testEntry));
        when(userStatsEntryRepository.save(any(UserStatsEntry.class))).thenReturn(testEntry);

        UserStatsStatus result = userStatsEntryService.checkHabitByUser("2", "1");

        assertEquals(UserStatsStatus.OPEN, result); // Expecting the status to change to OPEN
        assertEquals(UserStatsStatus.OPEN, testEntry.getStatus()); // Verify the status was updated in the repository
    }
    @Test
    void checkHabitByUser_conflict() {
        UserStatsEntry testEntry = new UserStatsEntry();
        testEntry.setStatus(UserStatsStatus.FAIL);
        when(userStatsEntryRepository.findByUserIdAndHabitIdAndDueDate("1", "2", LocalDate.now())).thenReturn(Optional.of(testEntry));

        assertThrows(ResponseStatusException.class, () -> userStatsEntryService.checkHabitByUser("2", "1"));
    }

    @Test
    void allEntriesSuccess_success() {
        when(userStatsEntryRepository.countByHabitIdAndDueDateAndStatusNot("1", LocalDate.now(), UserStatsStatus.SUCCESS)).thenReturn(0L);
        boolean result = userStatsEntryService.allEntriesSuccess("1", LocalDate.now());
        assertTrue(result);
    }
    @Test
    void allEntriesSuccess_someNotSuccess() {
        String habitId = "habit1";
        LocalDate date = LocalDate.now();
        when(userStatsEntryRepository.countByHabitIdAndDueDateAndStatusNot(habitId, date, UserStatsStatus.SUCCESS)).thenReturn(1L);

        boolean result = userStatsEntryService.allEntriesSuccess(habitId, date);

        assertFalse(result);
    }

    @Test
    void entriesExist_success() {
        when(userStatsEntryRepository.existsByHabitIdAndDueDate("1", LocalDate.now())).thenReturn(true);

        boolean result = userStatsEntryService.entriesExist("1", LocalDate.now());

        assertTrue(result);
    }
    @Test
    void getAllSuccessfulUsers_success() {
        UserStatsEntry testEntry = new UserStatsEntry();
        testEntry.setStatus(UserStatsStatus.SUCCESS);
        when(userStatsEntryRepository.findByHabitIdAndDueDateAndStatus("1", LocalDate.now(), UserStatsStatus.SUCCESS)).thenReturn(Arrays.asList(testEntry));

        List<UserStatsEntry> result = userStatsEntryService.getAllSuccessfulUsers("1", LocalDate.now());

        assertEquals(1, result.size());
        assertEquals(UserStatsStatus.SUCCESS, result.get(0).getStatus());
    }
    @Test
    void countUniqueHabitsByDate_success() {
        when(userStatsEntryRepository.countDistinctHabitIdsByDueDate(LocalDate.now())).thenReturn(2);

        Integer result = userStatsEntryService.countUniqueHabitsByDate(LocalDate.now());

        assertEquals(2, result);
    }
    @Test
    void deleteUserStatsEntriesOfToday_withEntries() {
        String habitId = "habit1";
        LocalDate today = LocalDate.now();
        UserStatsEntry entry1 = new UserStatsEntry();
        UserStatsEntry entry2 = new UserStatsEntry();
        List<UserStatsEntry> entries = Arrays.asList(entry1, entry2);

        when(userStatsEntryRepository.findByHabitIdAndDueDate(habitId, today)).thenReturn(entries);

        userStatsEntryService.deleteUserStatsEntriesOfToday(habitId);

        verify(userStatsEntryRepository, times(entries.size())).delete(Mockito.any(UserStatsEntry.class));
    }
    @Test
    void deleteUserStatsEntriesOfToday_noEntries() {
        String habitId = "habit1";
        LocalDate today = LocalDate.now();

        when(userStatsEntryRepository.findByHabitIdAndDueDate(habitId, today)).thenReturn(Collections.emptyList());

        userStatsEntryService.deleteUserStatsEntriesOfToday(habitId);

        verify(userStatsEntryRepository, times(0)).delete(Mockito.any(UserStatsEntry.class));
    }
    @Test
    void deleteUserStatsEntriesByGroupId_test() {
        String groupId = "group1";

        userStatsEntryService.deleteUserStatsEntriesByGroupId(groupId);

        verify(userStatsEntryRepository, times(1)).deleteByGroupId(groupId);
    }
    @Test
    void deleteByUserIdAndDueDate_test() {
        String userId = "user1";
        LocalDate dueDate = LocalDate.now();

        userStatsEntryService.deleteByUserIdAndDueDate(userId, dueDate);

        verify(userStatsEntryRepository, times(1)).deleteByUserIdAndDueDate(userId, dueDate);
    }
    @Test
    void deleteByGroupId_test() {
        String groupId = "group1";

        userStatsEntryService.deleteByGroupId(groupId);

        verify(userStatsEntryRepository, times(1)).deleteByGroupId(groupId);
    }
    @Test
    void countUniqueHabitsByGroupIdAndDate_success() {
        String groupId = "testGroup";
        LocalDate date = LocalDate.now(); // Using current date for simplicity
        int expectedCount = 5; // Example expected count

        when(userStatsEntryRepository.countDistinctHabitIdsByDueDateAndGroupId(date, groupId)).thenReturn(expectedCount);
        Integer actualCount = userStatsEntryService.countUniqueHabitsByGroupIdAndDate(groupId, date);
        assertEquals(expectedCount, actualCount, "The count of unique habits should match the expected value.");

        verify(userStatsEntryRepository, times(1)).countDistinctHabitIdsByDueDateAndGroupId(date, groupId);
    }

}
