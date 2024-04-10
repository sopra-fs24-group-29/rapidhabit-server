package ch.uzh.ifi.hase.soprafs24.entity;

import ch.uzh.ifi.hase.soprafs24.constant.RepeatType;

public class MonthlyRepeat implements RepeatStrategy{
    private int monthlyReps;

    public MonthlyRepeat(int monthlyReps) {
        setMonthlyReps(monthlyReps);
    }

    @Override
    public RepeatType getRepeatType(){
        return RepeatType.MONTHLY;
    }

    public int getMonthlyReps() {
        return monthlyReps;
    }

    public void setMonthlyReps(int monthlyReps) {
        if (monthlyReps <= 0) {
            throw new IllegalArgumentException("Invalid month interval: " + monthlyReps);
        }
        this.monthlyReps = monthlyReps;
    }
}
