package ch.uzh.ifi.hase.soprafs24.repository;

import ch.uzh.ifi.hase.soprafs24.entity.UserScore;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserScoreRepository extends MongoRepository<UserScore, String> {
    void deleteByUserIdAndGroupId(String userId, String groupId);
    Optional<UserScore> findByUserIdAndGroupId(String userId, String groupId);
}
