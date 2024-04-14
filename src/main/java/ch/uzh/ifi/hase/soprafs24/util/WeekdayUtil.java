package ch.uzh.ifi.hase.soprafs24.util;

import ch.uzh.ifi.hase.soprafs24.constant.Weekday;

import java.time.DayOfWeek;
import java.time.LocalDate;

public class WeekdayUtil {

    public static Weekday getCurrentWeekday() {
        DayOfWeek currentDayOfWeek = LocalDate.now().getDayOfWeek();
        switch (currentDayOfWeek) {
            case MONDAY:
                return Weekday.MONDAY;
            case TUESDAY:
                return Weekday.TUESDAY;
            case WEDNESDAY:
                return Weekday.WEDNESDAY;
            case THURSDAY:
                return Weekday.THURSDAY;
            case FRIDAY:
                return Weekday.FRIDAY;
            case SATURDAY:
                return Weekday.SATURDAY;
            case SUNDAY:
                return Weekday.SUNDAY;
            default:
                throw new IllegalStateException("Unknown Weekday: " + currentDayOfWeek);
        }
    }
}

