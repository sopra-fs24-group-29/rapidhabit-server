package ch.uzh.ifi.hase.soprafs24.repository;

import ch.uzh.ifi.hase.soprafs24.entity.FeedMessage;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository("feedMessageRepository")
public interface FeedMessageRepository extends MongoRepository<FeedMessage, String> {

    Optional<FeedMessage> findById(String id);
    // Custom query to find the latest FeedMessage of type 'PULSECHECK' for a specific group
    @Query(value = "{ 'groupId': ?0, 'type': 'PULSECHECK' }", sort = "{ 'dateTime': -1 }")
    Optional<FeedMessage> findLatestPulseCheckByGroupId(String groupId);

    @Query(value = "{ 'groupId': ?0, 'formId': ?1, 'type': 'PULSECHECK' }", sort = "{ 'dateTime': -1 }")
    Optional<FeedMessage> findLatestPulseCheckByGroupAndForm(String groupId, String formId);
}
