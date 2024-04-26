package ch.uzh.ifi.hase.soprafs24.repository;

import ch.uzh.ifi.hase.soprafs24.constant.UserStatsStatus;
import ch.uzh.ifi.hase.soprafs24.entity.UserStatsEntry;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface UserStatsEntryRepository extends MongoRepository<UserStatsEntry, String> {
    List<UserStatsEntry> findAllByDueDateLessThan(LocalDate date);
    List<UserStatsEntry> findAllByDueDateLessThanAndStatus(LocalDate date, UserStatsStatus status);
    List<UserStatsEntry> findAllByGroupId(String groupId);

    @Query("{'groupId': ?0, 'dueDate': ?1}")
    List<UserStatsEntry> findAllByGroupIdAndDueDate(String groupId, LocalDate dueDate);

    @Query("{'userId': ?0, 'habitId': ?1, 'dueDate': ?2}")
    Optional<UserStatsEntry> findByUserIdAndHabitIdAndDueDate(String userId, String habitId, LocalDate dueDate);

    @Query("{'userId': ?0, 'habitId': ?1, 'dueDate': {$gte: ?2, $lt: ?3}}")
    Optional<UserStatsEntry> findByUserIdAndHabitIdAndDueDateBetween(String userId, String habitId, Instant startOfDay, Instant endOfDay);

    @Query(value = "{'userId': ?0, 'habitId': ?1}", sort = "{'dueDate': -1}")
    Optional<UserStatsEntry> findLatestByUserIdAndHabitId(String userId, String habitId);

    List<UserStatsEntry> findByHabitIdAndGroupId(String habitId, String groupId);

    List<UserStatsEntry> findByUserIdAndHabitId(String userId, String habitId);

    @Query("{'groupId': ?0, 'habitId': ?1, 'dueDate': ?2}")
    List<UserStatsEntry> findByGroupIdAndHabitIdAndDueDate(String groupId, String habitId, LocalDate dueDate);

    @Query(value = "{'habitId': ?0, 'dueDate': ?1, 'status': {$ne: ?2}}", count = true)
    Long countByHabitIdAndDueDateAndStatusNot(String habitId, LocalDate dueDate, UserStatsStatus statusNot);


    boolean existsByHabitIdAndDueDate(String habitId, LocalDate dueDate);
    List<UserStatsEntry> findByHabitIdAndDueDateAndStatus(String habitId, LocalDate dueDate, UserStatsStatus status);

    @Aggregation(pipeline = {
            "{ $match: { 'dueDate': ?0 } }",
            "{ $group: { _id: '$habitId' } }",
            "{ $count: 'distinctHabitCount' }"
    })
    Integer countDistinctHabitIdsByDueDate(LocalDate dueDate);

    @Query("{'habitId': ?0, 'dueDate': ?1}")
    List<UserStatsEntry> findByHabitIdAndDueDate(String habitId, LocalDate dueDate);

    void deleteByGroupId(String groupId);

    void deleteByUserIdAndDueDate(String userId, LocalDate dueDate);

}
