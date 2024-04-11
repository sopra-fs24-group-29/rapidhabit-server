package ch.uzh.ifi.hase.soprafs24.entity;
import ch.uzh.ifi.hase.soprafs24.constant.Daytime;
import ch.uzh.ifi.hase.soprafs24.constant.RepeatType;
import java.util.HashMap;

public class DailyRepeat implements RepeatStrategy{
    private Boolean repeats;

    public DailyRepeat(Boolean repeats) {
        this.repeats = repeats;
    }

    @Override
    public RepeatType getRepeatType(){
        return RepeatType.DAILY;
    }

    public void setRepeats(Boolean value){
        this.repeats = value;
    }

    public Boolean getRepeats(){
        return this.repeats;
    }
}