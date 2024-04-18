package ch.uzh.ifi.hase.soprafs24.service;
import ch.uzh.ifi.hase.soprafs24.entity.Habit;
import ch.uzh.ifi.hase.soprafs24.repository.HabitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class HabitService {

    private final HabitRepository habitRepository;

    @Autowired
    public HabitService(HabitRepository habitRepository) {
        this.habitRepository = habitRepository;
    }

    public Habit createHabit(Habit habit) {
        return habitRepository.save(habit);
    }

    public List<Habit> getAllHabits() {
        return habitRepository.findAll();
    }

    public Optional<Habit> getHabitById(String id) {
        return habitRepository.findById(id);
    }

    public Habit updateHabit(String id, Habit updatedHabit) {
        if (habitRepository.existsById(id)) {
            updatedHabit.setId(id);
            return habitRepository.save(updatedHabit);
        }
        return null;
    }

    public void deleteHabit(String id) {
        habitRepository.deleteById(id);
    }
    public void incrementCurrentStreak(String habitId) {
        Habit habit = habitRepository.findById(habitId)
                .orElseThrow(() -> new IllegalArgumentException("Habit not found with id: " + habitId));

        habit.setCurrentStreak(habit.getCurrentStreak() + 1);  // Increment the current streak
        habitRepository.save(habit);  // Save the updated habit
    }

    public int getStreak(String habitId){
        Habit habit = habitRepository.findById(habitId)
                .orElseThrow(() -> new IllegalArgumentException("Habit not found with id: " + habitId));
        return habit.getCurrentStreak();
    }
}
