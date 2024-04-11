package ch.uzh.ifi.hase.soprafs24.entity;

import ch.uzh.ifi.hase.soprafs24.constant.RepeatType;
import ch.uzh.ifi.hase.soprafs24.constant.Weekday;

import java.util.HashMap;
import java.util.Map;

public class WeeklyRepeat implements RepeatStrategy{
    private HashMap<Weekday, Boolean> weekdayMap = new HashMap<>();

    public WeeklyRepeat() {
        // Initialisiere weekdayMap mit Werten für alle Wochentage als "true".
        for (Weekday weekday : Weekday.values()) {
            weekdayMap.put(weekday, true);
        }
    }

    @Override
    public RepeatType getRepeatType(){
        return RepeatType.WEEKLY;
    }

    public HashMap<Weekday, Boolean> getWeekdayMap() {
        return this.weekdayMap;
    }

    public void setWeekdayToRepeat(Weekday weekday, Boolean repeats) {
        weekdayMap.put(weekday, repeats);
    }

    public void setWeekdayMap(Map<Weekday, Boolean> weekdayMap) {
        this.weekdayMap.clear();
        this.weekdayMap.putAll(weekdayMap);
    }
}
