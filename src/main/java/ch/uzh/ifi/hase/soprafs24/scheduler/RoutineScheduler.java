
package ch.uzh.ifi.hase.soprafs24.scheduler;

import ch.uzh.ifi.hase.soprafs24.constant.RepeatType;
import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.constant.Weekday;
import ch.uzh.ifi.hase.soprafs24.entity.*;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs24.service.*;
import ch.uzh.ifi.hase.soprafs24.util.WeekdayUtil;
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

    private final UserService userService;

    private final UserStatsEntryService userStatsEntryService;

    private final UserScoreService userScoreService;

    private final UserRepository userRepository;

    RoutineScheduler(GroupService groupService, HabitService habitService, UserStatsEntryService userStatsEntryService, UserRepository userRepository, UserService userService, UserScoreService userScoreService){
        this.groupService = groupService;
        this.habitService = habitService;
        this.userStatsEntryService = userStatsEntryService;
        this.userRepository = userRepository;
        this.userService = userService;
        this.userScoreService = userScoreService;
    }

    @Scheduled(cron = "0 0 0 * * ?") // Executes every day at midnight
    public void checkAndScheduleHabitRoutines() {
        System.out.println("Systemzeitzone: " + ZoneId.systemDefault());
        Weekday currentWeekday = WeekdayUtil.getCurrentWeekday();
        System.out.println(currentWeekday);
        System.out.println("Today, it is: " +currentWeekday +".");
        LocalDate today = LocalDate.now();
        System.out.println(today);

        // Update open entries which are expired to FAIL
        userStatsEntryService.updateStatusForExpiredEntries(today);

        List<Group> groups = groupService.getGroups();
        for (Group i : groups){
            String groupId = i.getId();
            Group group = groupService.getGroupById(groupId);
            List<String> groupUserIds = i.getUserIdList();
            List<String> habitIds = i.getHabitIdList();
            List<String> userIds = i.getUserIdList();

            // Iterate through each habit of the current group:
            Integer successfulHabits = 0;
            for (String habitId : habitIds){
                // retrieve the habit object
                Habit habit = habitService.getHabitById(habitId).orElseThrow(() -> new RuntimeException("No habit with id" +habitId +" was found."));
                // compute habit streaks
                if(userStatsEntryService.entriesExist(habitId, today)){ // if the habit was on schedule today
                    if(userStatsEntryService.allEntriesSuccess(habitId, today)){ // if all corresponding entries are assigned to success
                        habitService.incrementCurrentStreak(habitId); // increase habit streak of group
                        successfulHabits += 1; // increase the number of successfully completed habits
                    }

                    // now compute points for each user for the habit

                    int rewardPoints = habit.getRewardPoints();
                    for(String userId : userIds){ // for each user
                        User user = userService.getUserDetails(userId);
                        userScoreService.updatePoints(userId,groupId, habit);
                    }
                }

                // at this stage, identify if the habit has to be scheduled at the current day
                RepeatType repeatType = habit.getRepeatStrategy().getRepeatType();
                System.out.println(habitId +" has type " +repeatType);

                if (repeatType.equals(RepeatType.DAILY)){ // If the type of the habit is DAILY ...
                    // create an user stats entry for each user of this group refered to the current habitId
                    for (String userId : groupUserIds){
                        UserStatsEntry userStatsEntry = userStatsEntryService.createUserStatsEntry(userId, groupId,habitId);
                        System.out.println(userStatsEntry.getDueDate());
                    }
                }
                else if (repeatType.equals(RepeatType.WEEKLY)) { // If the type of the habit is WEEKLY ...
                    // ... Check if habit takes place at current weekday
                    if(habit.getRepeatStrategy().repeatsAt(currentWeekday)){
                        // create an user stats entry for each user of this group refered to the current habitId
                        for (String userId : groupUserIds){
                            userStatsEntryService.createUserStatsEntry(userId, groupId, habitId);
                        }
                    }
                }
            }
            // before moving on to the next group compute the group streak
            if(successfulHabits.equals(habitIds.size())){ // if the number of successful habits equals the number of all habits in the group
                groupService.incrementCurrentStreak(groupId); // count up the current group streak
            }
            userScoreService.updateRanksInGroup(groupId); // and update the rank of each user
            // now repeat the same process with the next group
        }
    }
}
