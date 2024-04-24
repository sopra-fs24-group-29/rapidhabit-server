
package ch.uzh.ifi.hase.soprafs24.scheduler;

import ch.uzh.ifi.hase.soprafs24.constant.FeedType;
import ch.uzh.ifi.hase.soprafs24.constant.PulseCheckStatus;
import ch.uzh.ifi.hase.soprafs24.constant.RepeatType;
import ch.uzh.ifi.hase.soprafs24.constant.Weekday;
import ch.uzh.ifi.hase.soprafs24.controller.MessageController;
import ch.uzh.ifi.hase.soprafs24.entity.*;
import ch.uzh.ifi.hase.soprafs24.service.*;
import ch.uzh.ifi.hase.soprafs24.util.FormIdGenerator;
import ch.uzh.ifi.hase.soprafs24.util.WeekdayUtil;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;


@Component
public class RoutineScheduler {
    private final GroupService groupService;
    private final HabitService habitService;

    private final UserStatsEntryService userStatsEntryService;

    private final UserScoreService userScoreService;

    private final PulseCheckEntryService pulseCheckEntryService;

    private final MessageController messageController;

    private final FeedMessageService feedMessageService;

    int count = 1;

    RoutineScheduler(GroupService groupService, HabitService habitService, UserStatsEntryService userStatsEntryService, UserService userService, UserScoreService userScoreService, PulseCheckEntryService pulseCheckEntryService, MessageController messageController, FeedMessageService feedMessageService){
        this.groupService = groupService;
        this.habitService = habitService;
        this.userStatsEntryService = userStatsEntryService;
        this.userScoreService = userScoreService;
        this.pulseCheckEntryService = pulseCheckEntryService;
        this.messageController = messageController;
        this.feedMessageService = feedMessageService;
    }


    @Scheduled(cron = "0 25 0 * * ?") // Opening Pulse Check entries at 10:15 AM for each group
    public void openMorningPulseCheck() {
        LocalDateTime creationTimestamp = LocalDateTime.now();
        LocalDateTime submissionTimestamp = LocalDateTime.now().plusMinutes(30);
        // Generate content for pulse check:
        String content = "Feed Content of Morning Pulse Check";
        List<Group> groupsList = groupService.getGroups();
        for(Group group : groupsList){
            String groupId = group.getId();
            String formId = FormIdGenerator.generateFormId(); // each group has the same formId
            List<String> userIdList = group.getUserIdList();
            for(String userId : userIdList){
                pulseCheckEntryService.createPulseCheckEntry(formId, groupId, userId, content, creationTimestamp, submissionTimestamp, PulseCheckStatus.OPEN);
            }
            String feedMessageText = "A new day brings new opportunities! How motivated are you today to complete all the habits in the group and boost the group's streak?";
            FeedMessage feedMessage = new FeedMessage(groupId, groupService.getGroupById(groupId).getName(),feedMessageText, FeedType.PULSECHECK, creationTimestamp); // Create Feed Message
            messageController.sendFeedToGroup(groupId, feedMessage);
            feedMessageService.createFeedMessage(feedMessage); // Store Feed Message within database
        }
    }




    @Scheduled(cron = "0 56 15 * * ?") // Corrected to run at 10:45 AM every day
    public void closeMorningPulseCheck() {
        List<Group> groupsList = groupService.getGroups();
        for (Group group : groupsList) {
            String groupId = group.getId();
            System.out.println("PROCESSING PULSE CHECK ENTRIES OF GROUP "+groupId+".");
            List<PulseCheckEntry> pulseCheckEntries = pulseCheckEntryService.findByGroupIdWithLatestEntryDate(groupId);
            System.out.println(pulseCheckEntries.size() +" entries were found for the last pulse check");
            List<Double> formDataList = new ArrayList<>(); // collects values of accepted pulse check entries
            int numberAcceptedEntries = 0;
            int numberRejectedEntries = 0;

            FeedMessage groupFeedMessage = feedMessageService.getLatestPulseCheckMessage(groupId);
            for (PulseCheckEntry entry : pulseCheckEntries) {
                if (entry.getStatus().equals(PulseCheckStatus.ACCEPTED)) {
                    formDataList.add(entry.getValue());
                    groupFeedMessage.addUserSubmits(entry.getUserId(),entry.getValue()); // Add value to corresponding FeedMessage for rendering the deactivated slider
                    numberAcceptedEntries++;
                } else if (entry.getStatus().equals(PulseCheckStatus.OPEN)) {
                    pulseCheckEntryService.setPulseCheckEntryStatus(entry, PulseCheckStatus.REJECTED);// set pulse check entry to rejected if still open after the deadline
                    Double defaultValue = 0.5;
                    groupFeedMessage.addUserSubmits(entry.getUserId(),defaultValue); // setting slider props to 0.5 which is the standard config for rendering unanswered pulse check boxes.
                    numberRejectedEntries++;
                }
            }

            // statistics
            double sum = formDataList.stream().mapToDouble(Double::doubleValue).sum();
            double average = formDataList.isEmpty() ? 0.0 : sum / formDataList.size();

            String outputStatement = "System.out.println(\"The form \" + pulseCheckEntries.get(0).getFormId() +\n" +
                    "                    \" with content '\" + pulseCheckEntries.get(0).getContent() +\n" +
                    "                    \"' was answered by \" + numberAcceptedEntries +\n" +
                    "                    \" of \" + group.getUserIdList().size() +\n" +
                    "                    \" users. The average rating was \" + average + \".\");";
            FeedMessage outputFeedMessage = new FeedMessage(groupId, groupService.getGroupById(groupId).getName(), outputStatement, FeedType.STANDARD, LocalDateTime.now());
            messageController.sendFeedToGroup(groupId,outputFeedMessage);
            feedMessageService.createFeedMessage(outputFeedMessage); // Store Output Feed Message within database
        }
    }


    @Scheduled(cron = "0 0 0 * * ?") // Triggered every day at midnight
    public void checkAndScheduleHabitRoutines() {
        System.out.println("Systemzeitzone: " + ZoneId.systemDefault());
        Weekday currentWeekday = WeekdayUtil.getCurrentWeekday();
        System.out.println(currentWeekday);
        System.out.println("Today, it is: " +currentWeekday +".");
        LocalDate today = LocalDate.now();
        LocalDate yesterday = LocalDate.now().minusDays(1);
        System.out.println(yesterday);

        // Update yesterday's open entries which are expired to FAIL
        userStatsEntryService.updateStatusForExpiredEntries(today);

        List<Group> groups = groupService.getGroups();
        for (Group i : groups){
            String groupId = i.getId();
            Group group = groupService.getGroupById(groupId);
            List<String> groupUserIds = i.getUserIdList();
            List<String> habitIds = i.getHabitIdList();
            List<String> userIds = i.getUserIdList();
            // Set counter for successful habits to zero
            Integer habitTargetNumber = userStatsEntryService.countUniqueHabitsByDate(yesterday);
            System.out.println("The number of habits to be completed by the past day was: "+habitTargetNumber);
            Integer successfulHabits = 0;

            // Iterate through each habit of the current group:
            for (String habitId : habitIds){
                // reset counter
                // retrieve the habit object
                Habit habit = habitService.getHabitById(habitId).orElseThrow(() -> new RuntimeException("No habit with id" +habitId +" was found."));
                // compute habit streaks
                if(userStatsEntryService.entriesExist(habitId, yesterday)){ // if the habit was on schedule the past day
                    if(userStatsEntryService.allEntriesSuccess(habitId, yesterday)){ // if all corresponding entries are assigned to success
                        System.out.println(habit.getName() +" was completed by all users. Habit streak is incremented");
                        habitService.incrementCurrentStreak(habitId); // increase habit streak of group
                        successfulHabits += 1; // increase the number of successfully completed habits
                    }
                    else {
                        System.out.println(habit.getName() +" was not completed by all users. Habit streak is reset.");
                        habitService.resetCurrentStreak(habitId);
                    }

                    // now compute points for each user for the habit
                    List<UserStatsEntry> successfulUsers = userStatsEntryService.getAllSuccessfulUsers(habitId, yesterday);
                    successfulUsers.forEach(entry -> {
                        userScoreService.updatePoints(entry.getUserId(), groupId, habit);
                    });

                // at this stage, identify if the habit has to be scheduled at the new day / current weekday
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
            if (successfulHabits.equals(habitTargetNumber) && habitTargetNumber > 0){ // if the number of successful habits equals the number of all habits in the group and is larger 0.
                groupService.incrementCurrentStreak(groupId); // count up the current group streak
            }
            else {
                groupService.resetCurrentStreak(groupId);
            }
            userScoreService.updateRanksInGroup(groupId); // and update the rank of each user
            // now repeat the same process with the next group
        }
    }
        System.out.println("Tagesabschluss beendet.");
    }
}
