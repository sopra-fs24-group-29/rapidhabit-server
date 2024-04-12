package ch.uzh.ifi.hase.soprafs24.entity;

import ch.uzh.ifi.hase.soprafs24.constant.RepeatType;
import ch.uzh.ifi.hase.soprafs24.constant.Weekday;

import java.util.HashMap;
import java.util.Map;

public class WeeklyRepeat implements RepeatStrategy{
    private HashMap<Weekday, Boolean> weekdayMap = new HashMap<>();

    public WeeklyRepeat() {
        // Initialize weekdayMap where all values for each weekday are "false" by default.
        this.weekdayMap = weekdayMap;
        weekdayMap.put(Weekday.MONDAY, false);
        weekdayMap.put(Weekday.TUESDAY, false);
        weekdayMap.put(Weekday.WEDNESDAY, false);
        weekdayMap.put(Weekday.THURSDAY, false);
        weekdayMap.put(Weekday.FRIDAY, false);
        weekdayMap.put(Weekday.SATURDAY, false);
        weekdayMap.put(Weekday.SUNDAY, false);
    }

    @Override
    public RepeatType getRepeatType(){
        return RepeatType.WEEKLY;
    }

    @Override
    public Boolean repeatsAt(Weekday weekday) {
        return Boolean.TRUE.equals(weekdayMap.get(weekday));
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
