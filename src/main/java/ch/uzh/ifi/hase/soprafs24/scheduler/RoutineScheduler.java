package ch.uzh.ifi.hase.soprafs24.scheduler;

import ch.uzh.ifi.hase.soprafs24.constant.RepeatType;
import ch.uzh.ifi.hase.soprafs24.constant.Weekday;
import ch.uzh.ifi.hase.soprafs24.entity.Group;
import ch.uzh.ifi.hase.soprafs24.entity.Habit;
import ch.uzh.ifi.hase.soprafs24.entity.UserStatsEntry;
import ch.uzh.ifi.hase.soprafs24.service.GroupService;
import ch.uzh.ifi.hase.soprafs24.service.HabitService;
import ch.uzh.ifi.hase.soprafs24.service.UserStatsEntryService;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.DayOfWeek;

import java.time.ZoneId;
import java.util.List;

@Component
public class RoutineScheduler {
    private final GroupService groupService;
    private final HabitService habitService;

    private final UserStatsEntryService userStatsEntryService;

    RoutineScheduler(GroupService groupService, HabitService habitService, UserStatsEntryService userStatsEntryService){
        this.groupService = groupService;
        this.habitService = habitService;
        this.userStatsEntryService = userStatsEntryService;
    }

    @Scheduled(fixedDelay = 10000) // Executes every 10 seconds, as an example
    public void sendNotifications() {
        // System.out.println("A new notification was sent.");
    }

    @Scheduled(cron = "0 45 13 * * ?") // Executes every day at 17:30
    public void checkAndScheduleHabitRoutines() {
        System.out.println("Systemzeitzone: " + ZoneId.systemDefault());
        Weekday currentWeekday = getCurrentWeekday();
        System.out.println(currentWeekday);
        System.out.println("Today, it is: " +currentWeekday +".");
        LocalDate today = LocalDate.now();
        System.out.println(today);

        // Update open entries which are expired to FAIL
        userStatsEntryService.updateStatusForExpiredEntries(today);

        List<Group> groups = groupService.getGroups();
        for (Group i : groups){
            String groupId = i.getId();
            List<String> groupUserIds = i.getUserIdList();
            List<String> habitIds = i.getHabitIdList();
            // Implement the following steps:
            for (String habitId : habitIds){
                Habit habit = habitService.getHabitById(habitId).orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "No habit with id " + groupId + " found."));
                RepeatType repeatType = habit.getRepeatStrategy().getRepeatType();
                System.out.println(habitId +" has type " +repeatType);
                if (repeatType.equals(RepeatType.DAILY)){
                    // create an user stats entry for each user of this group refered to the current habitId
                    for (String userId : groupUserIds){
                        UserStatsEntry userStatsEntry = userStatsEntryService.createUserStatsEntry(userId, groupId,habitId);
                        System.out.println(userStatsEntry.getDueDate());
                    }
                }
                else if (repeatType.equals(RepeatType.WEEKLY)) {
                    // ... Check if habit takes place at current weekday
                    if(habit.getRepeatStrategy().repeatsAt(currentWeekday)){
                        // create an user stats entry for each user of this group refered to the current habitId
                        for (String userId : groupUserIds){
                            userStatsEntryService.createUserStatsEntry(userId, groupId, habitId);
                        }
                    }
                }
            }
        }
    }

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
