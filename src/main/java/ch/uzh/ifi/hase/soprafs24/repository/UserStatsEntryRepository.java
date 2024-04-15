package ch.uzh.ifi.hase.soprafs24.repository;

import ch.uzh.ifi.hase.soprafs24.constant.UserStatsStatus;
import ch.uzh.ifi.hase.soprafs24.entity.UserStatsEntry;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface UserStatsEntryRepository extends MongoRepository<UserStatsEntry, String> {
    List<UserStatsEntry> findByUserId(String userId);
    List<UserStatsEntry> findAllByDueDateLessThan(LocalDate date);
    List<UserStatsEntry> findAllByDueDateLessThanAndStatus(LocalDate date, UserStatsStatus status);
    List<UserStatsEntry> findAllByGroupId(String groupId);

    // Existing custom query
    @Query("{'groupId': ?0, 'dueDate': ?1}")
    List<UserStatsEntry> findAllByGroupIdAndDueDate(String groupId, LocalDate dueDate);

    // New function to find entries by userId, habitId, and specific LocalDate
    @Query("{'userId': ?0, 'habitId': ?1, 'dueDate': ?2}")
    Optional<UserStatsEntry> findByUserIdAndHabitIdAndDueDate(String userId, String habitId, LocalDate dueDate);

    List<UserStatsEntry> findByHabitIdAndGroupId(String habitId, String groupId);

    List<UserStatsEntry> findByUserIdAndHabitId(String userId, String habitId);

}
