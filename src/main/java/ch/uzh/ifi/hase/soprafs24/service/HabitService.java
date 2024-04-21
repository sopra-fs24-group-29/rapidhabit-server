package ch.uzh.ifi.hase.soprafs24.service;
import ch.uzh.ifi.hase.soprafs24.constant.Weekday;
import ch.uzh.ifi.hase.soprafs24.entity.Habit;
import ch.uzh.ifi.hase.soprafs24.repository.HabitRepository;
import ch.uzh.ifi.hase.soprafs24.rest.dto.habit.HabitPutDTO;
import ch.uzh.ifi.hase.soprafs24.util.WeekdayUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
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
    public void resetCurrentStreak(String habitId){
        Habit habit = habitRepository.findById(habitId)
                .orElseThrow(() -> new IllegalArgumentException("Habit not found with id: " + habitId));
        habit.setCurrentStreak(0);
        habitRepository.save(habit);
    }

    public int getStreak(String habitId){
        Habit habit = habitRepository.findById(habitId)
                .orElseThrow(() -> new IllegalArgumentException("Habit not found with id: " + habitId));
        return habit.getCurrentStreak();
    }
    public Boolean isCurrentWeekdayActive(Habit habit) {
        // Ermittle den aktuellen Wochentag
        Weekday currentWeekday = WeekdayUtil.getCurrentWeekday();
        // Hole die weekdayMap aus der repeatStrategy des Habits
        return habit.getRepeatStrategy().repeatsAt(currentWeekday);
}
}
