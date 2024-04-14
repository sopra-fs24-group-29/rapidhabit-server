package ch.uzh.ifi.hase.soprafs24.repository;

import ch.uzh.ifi.hase.soprafs24.entity.HabitStreak;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface HabitStreakRepository extends MongoRepository<HabitStreak, String> {
    void deleteByGroupIdAndHabitId(String groupId, String habitId);
    Optional<HabitStreak> findByHabitIdAndGroupId(String habitId, String groupId);
}
