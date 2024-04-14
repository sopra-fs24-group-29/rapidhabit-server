package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.entity.HabitStreak;
import ch.uzh.ifi.hase.soprafs24.repository.HabitStreakRepository;
import org.springframework.stereotype.Service;

@Service
public class HabitStreakService {

    private final HabitStreakRepository repository;

    public HabitStreakService(HabitStreakRepository repository) {
        this.repository = repository;
    }

    // Example method to add a new HabitStreak
    public HabitStreak addHabitStreak(HabitStreak habitStreak) {
        return repository.save(habitStreak);
    }
}
