package ch.uzh.ifi.hase.soprafs24.repository;

import ch.uzh.ifi.hase.soprafs24.entity.HabitStreak;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface HabitStreakRepository extends MongoRepository<HabitStreak, String> {
    void deleteByGroupIdAndHabitId(String groupId, String habitId);
}
