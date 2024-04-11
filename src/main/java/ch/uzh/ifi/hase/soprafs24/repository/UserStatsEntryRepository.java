package ch.uzh.ifi.hase.soprafs24.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import ch.uzh.ifi.hase.soprafs24.entity.UserStatsEntry;

import java.util.List;

public interface UserStatsEntryRepository extends MongoRepository<UserStatsEntry, String> {
    List<UserStatsEntry> findByUserId(String userId);
}
