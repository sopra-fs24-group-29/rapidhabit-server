package ch.uzh.ifi.hase.soprafs24.entity;
import ch.uzh.ifi.hase.soprafs24.constant.RepeatType;
import ch.uzh.ifi.hase.soprafs24.constant.Weekday;

public class DailyRepeat implements RepeatStrategy{

    public DailyRepeat() {
    }

    @Override
    public RepeatType getRepeatType(){
        return RepeatType.DAILY;
    }

    @Override
    public Boolean repeatsAt(Weekday weekday){
        return true;
    }
}