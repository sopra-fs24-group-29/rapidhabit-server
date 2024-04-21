package ch.uzh.ifi.hase.soprafs24.repository;

import ch.uzh.ifi.hase.soprafs24.entity.UserScore;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserScoreRepository extends MongoRepository<UserScore, String> {
    void deleteByUserIdAndGroupId(String userId, String groupId);
    void deleteByGroupId(String groupId);
    Optional<UserScore> findByUserIdAndGroupId(String userId, String groupId);

    @Query("{'groupId': ?0}")
    List<UserScore> findAllByGroupIdOrderByPointsDesc(String groupId);
}
