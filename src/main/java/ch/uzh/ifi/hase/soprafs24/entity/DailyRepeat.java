package ch.uzh.ifi.hase.soprafs24.entity;
import ch.uzh.ifi.hase.soprafs24.constant.Daytime;
import ch.uzh.ifi.hase.soprafs24.constant.RepeatType;
import java.util.HashMap;

public class DailyRepeat implements RepeatStrategy{
    private HashMap<Daytime, Integer> daytimeMap = new HashMap<>();

    public DailyRepeat() {
        for (Daytime daytime : Daytime.values()) {
            daytimeMap.put(daytime, 0);
        }
    }

    @Override
    public RepeatType getRepeatType(){
        return RepeatType.DAILY;
    }

    public HashMap<Daytime, Integer> getDaytimeMap() {
        return this.daytimeMap;
    }

    public void setDaytimeToRepeat(Daytime daytime, Integer repeats) {
        daytimeMap.put(daytime, repeats);
    }

    public Integer repeatsAtDaytime(Daytime daytime) {
        return daytimeMap.getOrDefault(daytime, 0);
    }

    public int getDailyReps() {
        return daytimeMap.values().stream().mapToInt(Integer::intValue).sum();
    }
}
