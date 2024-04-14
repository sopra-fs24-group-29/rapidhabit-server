package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.entity.HabitStreak;
import ch.uzh.ifi.hase.soprafs24.repository.HabitStreakRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
public class HabitStreakService {

    private final HabitStreakRepository habitStreakRepository;

    public HabitStreakService(HabitStreakRepository repository) {
        this.habitStreakRepository = repository;
    }

    public HabitStreak addHabitStreak(HabitStreak habitStreak) {
        return habitStreakRepository.save(habitStreak);
    }

    public int getStreak(String habitId, String groupId) {
        HabitStreak habitStreak = habitStreakRepository.findByHabitIdAndGroupId(habitId, groupId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No habit streak entry found."));
        return habitStreak.getStreak();
    }
}

