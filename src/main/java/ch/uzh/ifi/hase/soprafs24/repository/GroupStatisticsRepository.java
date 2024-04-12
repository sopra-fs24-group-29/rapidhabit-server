package ch.uzh.ifi.hase.soprafs24.repository;

import ch.uzh.ifi.hase.soprafs24.entity.Group;
import org.springframework.data.mongodb.repository.MongoRepository;
import ch.uzh.ifi.hase.soprafs24.entity.GroupStatistics;

import java.util.Optional;

public interface GroupStatisticsRepository extends MongoRepository<GroupStatistics, String> {
    Optional<GroupStatistics> findById(String id);
    Optional<GroupStatistics> findByGroupId(String groupId);
}

