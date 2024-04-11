package ch.uzh.ifi.hase.soprafs24.rest.dto.habit;

import ch.uzh.ifi.hase.soprafs24.constant.RepeatType;
import ch.uzh.ifi.hase.soprafs24.constant.Weekday;
import java.util.Map;

public class WeeklyRepeatDTO implements RepeatStrategyDTO {
    private Map<Weekday, Boolean> weekdayMap;

    @Override
    public RepeatType getRepeatType() {
        return RepeatType.WEEKLY;
    }

    public Map<Weekday, Boolean> getWeekdayMap() {
        return weekdayMap;
    }

    public void setWeekdayMap(Map<Weekday, Boolean> weekdayMap) {
        this.weekdayMap = weekdayMap;
    }

}
