package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.constant.UserStatsStatus;
import ch.uzh.ifi.hase.soprafs24.entity.Habit;
import ch.uzh.ifi.hase.soprafs24.entity.UserStatsEntry;
import ch.uzh.ifi.hase.soprafs24.repository.HabitRepository;
import ch.uzh.ifi.hase.soprafs24.repository.UserStatsEntryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserStatsEntryService {

    private final UserStatsEntryRepository userStatsEntryRepository;
    private final HabitRepository habitRepository;

    @Autowired
    public UserStatsEntryService(UserStatsEntryRepository userStatsEntryRepository, HabitRepository habitRepository) {
        this.userStatsEntryRepository = userStatsEntryRepository;
        this.habitRepository = habitRepository;
    }
    public UserStatsEntry createUserStatsEntry(String userId, String groupId, String habitId) {
        UserStatsEntry userStatsEntry = new UserStatsEntry();
        // userStatsEntry.setDueDate(LocalDate.now().minusDays(1)); // Test for yesterday
        userStatsEntry.setDueDate(LocalDate.now());
        userStatsEntry.setUserId(userId);
        userStatsEntry.setGroupId(groupId);
        userStatsEntry.setHabitId(habitId);
        userStatsEntry.setStatus(UserStatsStatus.OPEN);
        userStatsEntry = userStatsEntryRepository.save(userStatsEntry);
        return userStatsEntry;
    }

    public List<UserStatsEntry> findAll() {
        return userStatsEntryRepository.findAll();
    }

    public Optional<UserStatsEntry> findById(String id) {
        return userStatsEntryRepository.findById(id);
    }

    public UserStatsEntry save(UserStatsEntry userStatsEntry) {
        return userStatsEntryRepository.save(userStatsEntry);
    }

    public void deleteById(String id) {
        userStatsEntryRepository.deleteById(id);
    }

    public List<UserStatsEntry> findEntriesBeforeDate(LocalDate inputDate) {
        LocalDate dayBefore = inputDate.minusDays(1);
        return userStatsEntryRepository.findAllByDueDateLessThan(dayBefore);
    }

    @Transactional
    public void updateStatusForExpiredEntries(LocalDate inputDate) {

        List<UserStatsEntry> entries = userStatsEntryRepository.findAllByDueDateLessThanAndStatus(inputDate, UserStatsStatus.OPEN);

        entries.forEach(entry -> {
            entry.setStatus(UserStatsStatus.FAIL);
        });

        userStatsEntryRepository.saveAll(entries);
    }

    public Map<String, Integer> computeTeamStreak(String groupId) {
        List<UserStatsEntry> entries = userStatsEntryRepository.findAllByGroupId(groupId);
        Map<String, Integer> habitStreaks = new HashMap<>();

        entries.stream()
                .filter(entry -> entry.getStatus() == UserStatsStatus.SUCCESS)
                .forEach(entry -> {
                    String habitId = entry.getHabitId();
                    habitStreaks.putIfAbsent(habitId, 0);
                    habitStreaks.computeIfPresent(habitId, (key, val) -> val + 1);
                });

        return habitStreaks;
    }

    public Map<String, Map<String, String>> getGroupHabitStatus(String groupId) {
        List<UserStatsEntry> entries = userStatsEntryRepository.findAllByGroupIdAndDueDate(groupId, LocalDate.now());
        Map<String, Map<String, String>> habitStatusMap = new HashMap<>();

        entries.forEach(entry -> {
            String habitId = entry.getHabitId();
            String userId = entry.getUserId();
            String status = entry.getStatus().name(); // Gehe davon aus, dass Status ein Enum ist

            habitStatusMap.putIfAbsent(habitId, new HashMap<>());
            habitStatusMap.get(habitId).put(userId, status);
        });

        return habitStatusMap;
    }

    public Map<String, Integer> computeUserRanks(String groupId) {
        List<UserStatsEntry> entries = userStatsEntryRepository.findAllByGroupId(groupId);
        Map<String, Integer> userScores = new HashMap<>();

        entries.forEach(entry -> {
            if (entry.getStatus() == UserStatsStatus.SUCCESS) {
                // Punkte des Habits holen (angenommen, es gibt eine Methode getPointsForHabit(habitId))
                int points = getPointsForHabit(entry.getHabitId());
                userScores.merge(entry.getUserId(), points, Integer::sum);
            }
        });

        return userScores;
    }

    private int getPointsForHabit(String habitId) {
        Habit habit = habitRepository.findById(habitId).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "No habit with id " + habitId + " found."));
        return habit.getRewardPoints();
    }

    public Map<LocalDate, Boolean> getActivityData() {
        List<UserStatsEntry> entries = userStatsEntryRepository.findAll();
        Map<LocalDate, Boolean> activityMap = new HashMap<>();

        Map<LocalDate, List<UserStatsEntry>> entriesByDate = entries.stream()
                .collect(Collectors.groupingBy(UserStatsEntry::getDueDate));

        entriesByDate.forEach((date, entriesList) -> {
            boolean allSuccess = entriesList.stream()
                    .allMatch(entry -> entry.getStatus() == UserStatsStatus.SUCCESS);
            activityMap.put(date, allSuccess);
        });

        return activityMap;
    }
}
