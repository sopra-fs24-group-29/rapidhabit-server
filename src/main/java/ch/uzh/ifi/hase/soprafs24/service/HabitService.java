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
    private final GroupService groupService;

    @Autowired
    public HabitService(HabitRepository habitRepository, GroupService groupService) {
        this.habitRepository = habitRepository;
        this.groupService = groupService;
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

}
