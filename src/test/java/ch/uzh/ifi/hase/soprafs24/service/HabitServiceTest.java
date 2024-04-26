package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.entity.Habit;
import ch.uzh.ifi.hase.soprafs24.repository.HabitRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class HabitServiceTest {

    @Mock
    private HabitRepository habitRepository;
    @InjectMocks
    private HabitService habitService;

    @Test
    void createHabit_validInput_success() {
        Habit testHabit = new Habit();
        testHabit.setId("1");
        testHabit.setName("Test Habit");
        when(habitRepository.save(any(Habit.class))).thenReturn(testHabit);

        Habit result = habitService.createHabit(testHabit);

        assertEquals(testHabit.getId(), result.getId());
        assertEquals(testHabit.getName(), result.getName());
        verify(habitRepository, times(1)).save(any(Habit.class));
    }

    @Test
    void getAllHabits_success() {
        Habit testHabit1 = new Habit();
        testHabit1.setId("1");
        Habit testHabit2 = new Habit();
        testHabit2.setId("2");
        List<Habit> habitList = Arrays.asList(testHabit1, testHabit2);
        when(habitRepository.findAll()).thenReturn(habitList);

        List<Habit> result = habitService.getAllHabits();

        assertEquals(2, result.size());
        assertEquals(testHabit1.getId(), result.get(0).getId());
        assertEquals(testHabit2.getId(), result.get(1).getId());
    }
    @Test
    void getHabitById_success() {
        Habit testHabit = new Habit();
        testHabit.setId("1");
        testHabit.setName("Test Habit");
        when(habitRepository.findById("1")).thenReturn(Optional.of(testHabit));

        Optional<Habit> result = habitService.getHabitById("1");

        assertTrue(result.isPresent());
        assertEquals(testHabit.getId(), result.get().getId());
        assertEquals(testHabit.getName(), result.get().getName());
    }
    @Test
    void updateHabit_success() {
        Habit testHabit = new Habit();
        testHabit.setId("1");
        testHabit.setName("Test Habit");
        when(habitRepository.existsById("1")).thenReturn(true);
        when(habitRepository.save(any(Habit.class))).thenReturn(testHabit);

        Habit result = habitService.updateHabit("1", testHabit);

        assertEquals(testHabit.getId(), result.getId());
        assertEquals(testHabit.getName(), result.getName());
        verify(habitRepository, times(1)).save(any(Habit.class));
    }

    @Test
    void deleteHabit_success() {
        doNothing().when(habitRepository).deleteById("1");

        habitService.deleteHabit("1");

        verify(habitRepository, times(1)).deleteById("1");
    }

    @Test
    void incrementCurrentStreak_success() {
        Habit testHabit = new Habit();
        testHabit.setId("1");
        testHabit.setCurrentStreak(5);
        when(habitRepository.findById("1")).thenReturn(Optional.of(testHabit));
        when(habitRepository.save(any(Habit.class))).thenReturn(testHabit);

        habitService.incrementCurrentStreak("1");

        assertEquals(6, testHabit.getCurrentStreak());
        verify(habitRepository, times(1)).save(any(Habit.class));
    }
    @Test
    void resetCurrentStreak_success() {
        Habit testHabit = new Habit();
        testHabit.setId("1");
        testHabit.setCurrentStreak(5);
        when(habitRepository.findById("1")).thenReturn(Optional.of(testHabit));
        when(habitRepository.save(any(Habit.class))).thenReturn(testHabit);

        habitService.resetCurrentStreak("1");

        assertEquals(0, testHabit.getCurrentStreak());
        verify(habitRepository, times(1)).save(any(Habit.class));
    }
    @Test
    void getStreak_success() {
        Habit testHabit = new Habit();
        testHabit.setId("1");
        testHabit.setCurrentStreak(5);
        when(habitRepository.findById("1")).thenReturn(Optional.of(testHabit));

        int result = habitService.getStreak("1");

        assertEquals(5, result);
    }
}
