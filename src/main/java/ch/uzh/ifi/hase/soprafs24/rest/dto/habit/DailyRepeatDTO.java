package ch.uzh.ifi.hase.soprafs24.rest.dto.habit;

import ch.uzh.ifi.hase.soprafs24.constant.RepeatType;

// Eine Implementierung von RepeatStrategyDTO für tägliche Wiederholungen
public class DailyRepeatDTO implements RepeatStrategyDTO {

    @Override
    public RepeatType getRepeatType() {
        return RepeatType.DAILY;
    }

}
