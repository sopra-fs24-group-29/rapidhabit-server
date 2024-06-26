package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.constant.UserStatsStatus;
import ch.uzh.ifi.hase.soprafs24.entity.Habit;
import ch.uzh.ifi.hase.soprafs24.entity.UserStatsEntry;
import ch.uzh.ifi.hase.soprafs24.repository.HabitRepository;
import ch.uzh.ifi.hase.soprafs24.repository.UserStatsEntryRepository;
import ch.uzh.ifi.hase.soprafs24.rest.dto.habit.HabitDateUserStatusGetDTO;
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

    public Boolean habitChecked(String userId, String habitId) {
        Optional<UserStatsEntry> result = userStatsEntryRepository.findByUserIdAndHabitIdAndDueDate(userId, habitId, LocalDate.now());
        if (!result.isPresent()) {
            System.out.println("No entry found for User: " + userId + ", Habit: " + habitId);
        }
        else {
            System.out.println("Latest entry found: " + result.get());
        }
        return result.map(entry -> entry.getStatus().equals(UserStatsStatus.SUCCESS))
                .orElse(false);
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

    public List<HabitDateUserStatusGetDTO> getHabitData(String habitId, String groupId) {
        List<UserStatsEntry> entries = userStatsEntryRepository.findByHabitIdAndGroupId(habitId, groupId);

        return entries.stream()
                .map(entry -> new HabitDateUserStatusGetDTO(entry.getDueDate(), entry.getUserId(), entry.getStatus()))
                .collect(Collectors.toList());
    }

    public List<UserStatsEntry> getEntriesByUserIdAndHabitId(String userId, String habitId) {
        // Fetches all entries that match the given userId and habitId
        return userStatsEntryRepository.findByUserIdAndHabitId(userId, habitId);
    }

    public UserStatsStatus checkHabitByUser(String habitId, String userId) {
        UserStatsEntry userStatsEntry = userStatsEntryRepository.findByUserIdAndHabitIdAndDueDate(userId, habitId, LocalDate.now()).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "No data entry found."));

        if (userStatsEntry.getStatus().equals(UserStatsStatus.OPEN)) {
            userStatsEntry.setStatus(UserStatsStatus.SUCCESS);
            userStatsEntryRepository.save(userStatsEntry);
            return UserStatsStatus.SUCCESS;
        }
        else if (userStatsEntry.getStatus().equals(UserStatsStatus.SUCCESS)) {
            userStatsEntry.setStatus(UserStatsStatus.OPEN);
            userStatsEntryRepository.save(userStatsEntry);
            return UserStatsStatus.OPEN;
        }
        else {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "The status of the entry is neither OPEN nor SUCCESS.");
        }
    }

    public boolean allEntriesSuccess(String habitId, LocalDate date) {
        Long count = userStatsEntryRepository.countByHabitIdAndDueDateAndStatusNot(habitId, date, UserStatsStatus.SUCCESS);
        // Überprüfe, ob count null ist oder gleich 0
        if (count == null || count == 0) {
            return true;
        }
        else {
            return false;
        }
    }


    public boolean entriesExist(String habitId, LocalDate dueDate) {
        return userStatsEntryRepository.existsByHabitIdAndDueDate(habitId, dueDate);
    }

    @Transactional
    public List<UserStatsEntry> getAllSuccessfulUsers(String habitId, LocalDate date) {
        return userStatsEntryRepository.findByHabitIdAndDueDateAndStatus(habitId, date, UserStatsStatus.SUCCESS);
    }

    public Integer countUniqueHabitsByDate(LocalDate date) {
        return userStatsEntryRepository.countDistinctHabitIdsByDueDate(date);
    }

    public Integer countUniqueHabitsByGroupIdAndDate(String groupId, LocalDate date) {
        return userStatsEntryRepository.countDistinctHabitIdsByDueDateAndGroupId(date,groupId);
    }

    public void deleteUserStatsEntriesOfToday(String habitId) {
        LocalDate today = LocalDate.now();
        // Die Methode findByHabitIdAndDueDate muss List<UserStatsEntry> zurückgeben, nicht List<UserStatsStatus>
        List<UserStatsEntry> list = userStatsEntryRepository.findByHabitIdAndDueDate(habitId, today);
        // Keine zusätzliche geschweifte Klammer benötigt; for-Schleife sollte direkt nach der Deklaration folgen
        for (UserStatsEntry entry : list) {
            userStatsEntryRepository.delete(entry);
        }
    }

    public void deleteUserStatsEntriesByGroupId(String groupId) {
        userStatsEntryRepository.deleteByGroupId(groupId);
    }
    public void deleteByUserIdAndDueDate(String userId, LocalDate dueDate){
        userStatsEntryRepository.deleteByUserIdAndDueDate(userId, dueDate);
    }
    public void deleteByGroupId(String groupId){
        userStatsEntryRepository.deleteByGroupId(groupId);
    }
}
