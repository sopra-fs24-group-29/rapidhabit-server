package ch.uzh.ifi.hase.soprafs24.entity;

import ch.uzh.ifi.hase.soprafs24.constant.RepeatType;
import ch.uzh.ifi.hase.soprafs24.constant.Weekday;

import java.util.HashMap;

public class WeeklyRepeat implements RepeatStrategy{
    private HashMap<Weekday, Integer> weekdayMap = new HashMap<>();

    public WeeklyRepeat() {
        for (Weekday weekday : Weekday.values()) {
            weekdayMap.put(weekday, 0);
        }
    }

    @Override
    public RepeatType getRepeatType(){
        return RepeatType.WEEKLY;
    }

    public HashMap<Weekday, Integer> getWeekdayMap() {
        return this.weekdayMap;
    }

    public void setWeekdayToRepeat(Weekday weekday, Integer repeats) {
        weekdayMap.put(weekday, repeats);
    }

    public int getWeeklyReps() {
        return weekdayMap.values().stream().mapToInt(Integer::intValue).sum();
    }
}
