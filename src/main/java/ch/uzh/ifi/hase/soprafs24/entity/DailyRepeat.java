package ch.uzh.ifi.hase.soprafs24.entity;
import ch.uzh.ifi.hase.soprafs24.constant.Daytime;
import ch.uzh.ifi.hase.soprafs24.constant.RepeatType;
import java.util.HashMap;

public class DailyRepeat implements RepeatStrategy{

    public DailyRepeat() {
    }

    @Override
    public RepeatType getRepeatType(){
        return RepeatType.DAILY;
    }
}