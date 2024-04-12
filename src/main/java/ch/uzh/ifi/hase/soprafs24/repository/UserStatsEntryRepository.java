package ch.uzh.ifi.hase.soprafs24.repository;

import ch.uzh.ifi.hase.soprafs24.constant.UserStatsStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import ch.uzh.ifi.hase.soprafs24.entity.UserStatsEntry;
import org.springframework.data.mongodb.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface UserStatsEntryRepository extends MongoRepository<UserStatsEntry, String> {
    List<UserStatsEntry> findByUserId(String userId);
    List<UserStatsEntry> findAllByDueDateLessThan(LocalDate date);
    List<UserStatsEntry> findAllByDueDateLessThanAndStatus(LocalDate date, UserStatsStatus status);
    List<UserStatsEntry> findAllByGroupId(String groupId);

    // MongoDB Query, um Einträge nach Gruppen-ID und fälligem Datum zu finden
    @Query("{'groupId': ?0, 'dueDate': ?1}")
    List<UserStatsEntry> findAllByGroupIdAndDueDate(String groupId, LocalDate dueDate);
}
