package ch.uzh.ifi.hase.soprafs24.entity;

import ch.uzh.ifi.hase.soprafs24.constant.RepeatType;
import ch.uzh.ifi.hase.soprafs24.constant.Weekday;


public interface RepeatStrategy {
    public RepeatType getRepeatType();

    public Boolean repeatsAt(Weekday weekday);

}

