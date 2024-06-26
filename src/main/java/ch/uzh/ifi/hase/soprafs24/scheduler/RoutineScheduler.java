
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

import java.io.IOException;
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

    private final OpenAIService openAIService;

    int count = 1;

    RoutineScheduler(GroupService groupService, HabitService habitService, UserStatsEntryService userStatsEntryService, UserService userService, UserScoreService userScoreService, PulseCheckEntryService pulseCheckEntryService, MessageController messageController, FeedMessageService feedMessageService, OpenAIService openAIService){
        this.groupService = groupService;
        this.habitService = habitService;
        this.userStatsEntryService = userStatsEntryService;
        this.userScoreService = userScoreService;
        this.pulseCheckEntryService = pulseCheckEntryService;
        this.messageController = messageController;
        this.feedMessageService = feedMessageService;
        this.openAIService = openAIService;
    }


    @Scheduled(cron = "0 0 7 * * ?", zone = "Europe/Zurich") // Opening Pulse Check entries at 7:00 AM for each group
    public void openMorningPulseCheck() {
        LocalDateTime creationTimestamp = LocalDateTime.now();
        LocalDateTime submissionTimestamp = LocalDateTime.now().plusHours(5); // submission is at 12:00
        List<Group> groupsList = groupService.getGroups();
        for(Group group : groupsList){
            String groupId = group.getId();
            String formId = FormIdGenerator.generateFormId(); // each group has the same formId

            // Generate group content for pulse check:
            String content = "";
            boolean success = false;
            while (!success) {
                try {
                    content = openAIService.generatePulseCheckContentChatCompletion(40, 1);
                    System.out.println("Pulse Check Content: " + content);
                    success = true; // Setze den Erfolgsstatus auf true, wenn das Generieren erfolgreich ist
                } catch (IOException e) {
                    System.err.println("Fehler beim Generieren des Pulse Check Inhalts: " + e.getMessage());
                    e.printStackTrace();
                }
            }

            List<String> userIdList = group.getUserIdList();
            for(String userId : userIdList){
                pulseCheckEntryService.createPulseCheckEntry(formId, groupId, userId, content, creationTimestamp, submissionTimestamp, PulseCheckStatus.OPEN); // create a submission for for each group member
            }
            FeedMessage feedMessage = new FeedMessage(formId, groupId, groupService.getGroupById(groupId).getName(),content, FeedType.PULSECHECK, creationTimestamp); // Create Feed Message
            messageController.sendFeedToGroup(groupId, feedMessage); // Send FeedMessage to websocket channel of the group
            feedMessageService.createFeedMessage(feedMessage); // Store Feed Message within database
        }
    }

    @Scheduled(cron = "0 0 12 * * ?", zone = "Europe/Zurich") // To run at 12:00 PM every day
    public void closeMorningPulseCheck() {
        List<Group> groupsList = groupService.getGroups();
        for (Group group : groupsList) {
            String groupId = group.getId();
            System.out.println("PROCESSING PULSE CHECK ENTRIES OF GROUP " + groupId + ".");
            List<PulseCheckEntry> pulseCheckEntries = pulseCheckEntryService.findByGroupIdWithLatestEntryDate(groupId);
            System.out.println(pulseCheckEntries.size() + " entries were found for the last pulse check");
            String formId = pulseCheckEntries.get(0).getFormId(); // retrieve formId
            List<Double> formDataList = new ArrayList<>(); // collects values of accepted pulse check entries
            int numberAcceptedEntries = 0;
            int numberRejectedEntries = 0;

            FeedMessage groupFeedMessage = feedMessageService.getLatestPulseCheckMessage(groupId, formId);
            for (PulseCheckEntry entry : pulseCheckEntries) {
                if (entry.getStatus().equals(PulseCheckStatus.ACCEPTED)) {
                    System.out.println("Entry accepted.");
                    formDataList.add(entry.getValue());
                    feedMessageService.addUserSubmit(groupFeedMessage.getId(), entry.getUserId(), entry.getValue());
                    numberAcceptedEntries++;
                } else if (entry.getStatus().equals(PulseCheckStatus.OPEN)) {
                    System.out.println("Entry rejected");
                    pulseCheckEntryService.setPulseCheckEntryStatus(entry, PulseCheckStatus.REJECTED); // set pulse check entry to rejected if still open after the deadline
                    Double defaultValue = 0.5;
                    feedMessageService.addUserSubmit(groupFeedMessage.getId(), entry.getUserId(), defaultValue);
                    numberRejectedEntries++;
                }
            }

            // statistics
            double sum = formDataList.stream().mapToDouble(Double::doubleValue).sum();
            double average = formDataList.isEmpty() ? 0.0 : sum / formDataList.size();

            String outputStatement = String.format(
                    "The form with content '%s' was answered by %d of %d users. The average rating was %.2f.",
                    pulseCheckEntries.get(0).getContent(),
                    numberAcceptedEntries,
                    group.getUserIdList().size(),
                    average
            );

            FeedMessage outputFeedMessage = new FeedMessage(
                    formId,
                    groupId,
                    groupService.getGroupById(groupId).getName(),
                    outputStatement,
                    FeedType.STANDARD,
                    LocalDateTime.now()
            );

            messageController.sendFeedToGroup(groupId, outputFeedMessage);
            feedMessageService.createFeedMessage(outputFeedMessage); // Store Output Feed Message within database
        }
    }

    @Scheduled(cron = "0 0 18 * * *", zone = "Europe/Zurich") // Evening notification
    public void checkOpenHabitRoutines() {
        List<Group> groupsList = groupService.getGroups();
        for (Group group : groupsList) {
            String outputMessage = "";
            String groupId = group.getId();
            System.out.println("PROCESSING GROUP " + groupId + " FOR SENDING EVENING NOTIFICATION ...");
            List<String> habitIds = group.getHabitIdList();
            List<String> habitsSuccess = new ArrayList<>();
            List<String> habitsOpen = new ArrayList<>();

            if (habitIds.isEmpty()) {
                outputMessage = "Hello, " + group.getName() + "! It seems like there are no habits set for your group today. Please add some habits to track your progress.";
            } else {
                // Iterate through each habit
                for (String habitId : habitIds) {
                    Habit habit = habitService.getHabitById(habitId)
                            .orElseThrow(() -> new RuntimeException("No habit with id " + habitId + " was found."));
                    String habitName = habit.getName();
                    System.out.println("PROCESSING HABIT " + habitName + " ...");
                    if (userStatsEntryService.allEntriesSuccess(habitId, LocalDate.now())) {
                        habitsSuccess.add(habitName);
                    } else {
                        habitsOpen.add(habitName);
                    }
                }

                if (habitsOpen.isEmpty()) {
                    outputMessage = "Good job, " + group.getName() + "! Each member of your group has already checked all habits for today. Keep being consistent! ;)";
                } else {
                    outputMessage = "Good evening, team " + group.getName() + "! How is it going? For some member there are still some open habits to be checked: " +
                            String.join(", ", habitsOpen) + ". Finish all your tasks and your streak will be incremented by the end of the day. ;)";
                }
            }

            System.out.println(outputMessage);

            String formId = FormIdGenerator.generateFormId();
            FeedMessage outputFeedMessage = new FeedMessage(
                    formId,
                    groupId,
                    groupService.getGroupById(groupId).getName(),
                    outputMessage,
                    FeedType.STANDARD,
                    LocalDateTime.now()
            );

            messageController.sendFeedToGroup(groupId, outputFeedMessage);
            feedMessageService.createFeedMessage(outputFeedMessage); // Store Output Feed Message within database
        }
    }



    @Scheduled(cron = "0 0 0 * * ?", zone = "Europe/Zurich") // Triggered every day at midnight
    public void checkAndScheduleHabitRoutines() {
        System.out.println("Systemzeitzone: " + ZoneId.systemDefault());
        Weekday currentWeekday = WeekdayUtil.getCurrentWeekday();
        LocalDate today = LocalDate.now();
        LocalDate yesterday = LocalDate.now().minusDays(1);
        System.out.println("Yesteday's date was: " +yesterday +".");
        System.out.println("Today's date is: " +today +".");
        System.out.println("Today's weekday is: " +currentWeekday +".");

        // Update yesterday's open entries which are expired to FAIL
        userStatsEntryService.updateStatusForExpiredEntries(today); // today is correct: -> as the function tests due today

        List<Group> groups = groupService.getGroups();
        for (Group i : groups){
            System.out.println("Processing group: "+i.getName());
            String groupId = i.getId();
            Group group = groupService.getGroupById(groupId);
            List<String> groupUserIds = i.getUserIdList();
            List<String> habitIds = i.getHabitIdList();
            List<String> userIds = i.getUserIdList();
            // Set counter for successful habits to zero
            Integer habitTargetNumber = userStatsEntryService.countUniqueHabitsByGroupIdAndDate(groupId, yesterday);
            if (habitTargetNumber == null) {
                habitTargetNumber = 0; // Default value to avoid NullPointerException
            }
            Integer userTargetNumber = userIds.size();
            System.out.println("The number of habits to be completed by the past day was: " + habitTargetNumber);
            Integer successfulHabits = 0;


            // Iterate through each habit of the current group:
            for (String habitId : habitIds){
                // reset counter
                // retrieve the habit object
                Habit habit = habitService.getHabitById(habitId).orElseThrow(() -> new RuntimeException("No habit with id" +habitId +" was found."));
                // compute habit streaks
                if(userStatsEntryService.entriesExist(habitId, yesterday)){ // if the habit was on schedule the past day
                    System.out.println("Habit "+habit.getName() +" was on schedule yesterday.");
                    System.out.println("Checking if all corresponding userStats are set to SUCCESS...");
                    if(userStatsEntryService.allEntriesSuccess(habitId, yesterday)){ // if all corresponding entries are assigned to success

                        System.out.println(habit.getName() +" was completed by all users. Habit streak is incremented. 😄⬆️");
                        habitService.incrementCurrentStreak(habitId); // increase habit streak of group
                        successfulHabits += 1; // increase the number of successfully completed habits
                    }
                    else {
                        System.out.println(habit.getName() +" was not completed by all users. Habit streak is reset. 🥲⬇️");
                        habitService.resetCurrentStreak(habitId);
                    }

                    // now compute points for each user for the habit
                    List<UserStatsEntry> successfulUsers = userStatsEntryService.getAllSuccessfulUsers(habitId, yesterday);
                    successfulUsers.forEach(entry -> {
                        userScoreService.updatePoints(entry.getUserId(), groupId, habit);
                    });
                }
                else {
                    System.out.println("Habit "+habit.getName() +" was not on schedule yesterday.");
                }
                // at this stage, identify if the habit has to be scheduled at the new day / current weekday
                RepeatType repeatType = habit.getRepeatStrategy().getRepeatType();
                System.out.println(habit.getName() +" has type " +repeatType);

                if (repeatType.equals(RepeatType.DAILY)){ // If the type of the habit is DAILY ...
                    // create an user stats entry for each user of this group refered to the current habitId
                    for (String userId : groupUserIds){
                        UserStatsEntry userStatsEntry = userStatsEntryService.createUserStatsEntry(userId, groupId,habitId);
                        System.out.println("Opened new case for "+habit.getName() +" and user "+userId +" for today.");
                    }
                }
                else if (repeatType.equals(RepeatType.WEEKLY)) { // If the type of the habit is WEEKLY ...
                    // ... Check if habit takes place at current weekday
                    if(habit.getRepeatStrategy().repeatsAt(currentWeekday)){
                        System.out.println("Today is " +currentWeekday +". So the habit " +habit.getName() +" is scheduled for today.");
                        // create user stats entry for each user of this group refered to the current habitId
                        for (String userId : groupUserIds){
                            UserStatsEntry userStatsEntry = userStatsEntryService.createUserStatsEntry(userId, groupId,habitId);
                            System.out.println("Opened new case for "+habit.getName() +" and user "+userId +" for today.");
                        }
                    }
                    else {
                        System.out.println("Today is " +currentWeekday +". So the habit " +habit.getName() +" is not scheduled for today.");
                    }
                }
            }
            // before moving on to the next group compute the group streak
            if (habitTargetNumber > 0){ // if at least one habit was targeted due to yesterday
                if(successfulHabits.equals(habitTargetNumber)){ // if the number of successful habits equals the number of all habits in the group
                    System.out.println("✅ All necessary habits were checked by all members. 👩🏻‍🦰👨🏿👱🏼‍🧔🏽‍️👵🏼");
                    System.out.println("🔥⬆️ Group Streak is incremented ...");
                    groupService.incrementCurrentStreak(groupId); // count up the current group streak
                }
                else {
                    System.out.println("❌ Not all necessary habits were checked by all members. 👩🏻‍🦰👨🏿👱🏼‍🧔🏽‍️👵🏼");
                    System.out.println("💩️🥲 Group Streak is reset ...");
                    groupService.resetCurrentStreak(groupId);
                }
            }
            else { // if habitTargetNumber was 0 ...
                // Don't update streak
            }
            System.out.println("🏆 Update group ranks of group "+group.getName() +" ...");
            userScoreService.updateRanksInGroup(groupId); // and update the rank of each user
            // now repeat the same process with the next group
    }
        System.out.println("Tagesabschluss beendet.");
    }
}
