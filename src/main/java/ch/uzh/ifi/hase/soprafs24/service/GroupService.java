package ch.uzh.ifi.hase.soprafs24.service;
import ch.uzh.ifi.hase.soprafs24.entity.Group;
import ch.uzh.ifi.hase.soprafs24.entity.HabitStreak;
import ch.uzh.ifi.hase.soprafs24.entity.UserScore;
import ch.uzh.ifi.hase.soprafs24.repository.GroupRepository;
// import ch.uzh.ifi.hase.soprafs24.rest.dto.UserPostDTO; // wird verwendet um die User Daten aus der intern verwendeten DTO Representation zu lesen
import ch.uzh.ifi.hase.soprafs24.repository.HabitStreakRepository;
import ch.uzh.ifi.hase.soprafs24.repository.UserScoreRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

// import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.ArrayList;
import java.util.List;


/**
 * User Service
 * This class is the "worker" and responsible for all functionality related to
 * the user
 * (e.g., it creates, modifies, deletes, finds). The result will be passed back
 * to the caller.
 */
@Service
@Transactional
public class GroupService {

    private final Logger log = LoggerFactory.getLogger(GroupService.class);

    private final GroupRepository groupRepository;
    private final UserScoreRepository userScoreRepository;
    private final HabitStreakRepository habitStreakRepository;
    private final BCryptPasswordEncoder encoder;

    @Autowired
    public GroupService(GroupRepository groupRepository, BCryptPasswordEncoder encoder, UserScoreRepository userScoreRepository, HabitStreakRepository habitStreakRepository) {
        this.groupRepository = groupRepository;
        this.encoder = encoder;
        this.userScoreRepository = userScoreRepository;
        this.habitStreakRepository = habitStreakRepository;
    }

    // Deine Methoden hier...

    public Group createGroup(Group newGroup, String creatorId) {
        newGroup.setAccessCode(this.generateAccessCode());
        newGroup.setAdminIdList(new ArrayList<>());
        newGroup.setUserIdList(new ArrayList<>());
        newGroup.setHabitIdList(new ArrayList<>());
        newGroup.addAdminId(creatorId);
        newGroup.addUserId(creatorId);
        newGroup.setCurrentStreak(0);
        newGroup = groupRepository.save(newGroup);
        log.debug("Created Group: {}", newGroup);

        // Initialize UserScore Entry for creator of the group
        UserScore newUserScore = new UserScore();
        newUserScore.setUserId(newGroup.getUserIdList().get(0));
        newUserScore.setGroupId(newGroup.getId());
        newUserScore.setPoints(0);
        newUserScore.setRank(1);
        newUserScore = userScoreRepository.save(newUserScore);
        // Finally, return group object
        return newGroup;
    }

    public List<Group> getGroups() {
        return this.groupRepository.findAll();
    }

    public void addHabitIdToGroup(String groupId, String habitId) {
        Group group = groupRepository.findById(groupId).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "No group with id " + groupId + " found."));
        // Check if habit id is already present in the list
        if (!group.getHabitIdList().contains(habitId)) {
            group.getHabitIdList().add(habitId);
            groupRepository.save(group);
        }

        // Create new group streak entry for the corresponding habit
        HabitStreak habitStreak = new HabitStreak(groupId, habitId);
        habitStreak.setStreak(0);
        habitStreakRepository.save(habitStreak);
    }
    @Transactional
    public void removeHabitFromGroup(String groupId, String habitId) {
        // Fetch the group by groupId
        Group group = groupRepository.findById(groupId).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "No group with id " + groupId + " found."));

        // Check if the habit id is present in the list
        if (!group.getHabitIdList().contains(habitId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Habit does not exist in this group!");
        }

        // Remove the habit id from the list
        group.getHabitIdList().remove(habitId);
        groupRepository.save(group);

        // Delete the corresponding habit streak entry
        habitStreakRepository.deleteByGroupIdAndHabitId(groupId, habitId);
    }
    public String generateAccessCode(){
        // here the logic for generating the access code will be implemented
        // think about encoding the access code
        String accessCode = "AccessKey123";
        return accessCode;
    }
    public Boolean accessCodeValid(String accessCode, String groupId){
        Group group = groupRepository.findById(groupId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No group with id " + groupId + " found."));
        if(group.getAccessCode().equals(accessCode)){
            return true;
        }
        else {return false;}
    }

    public void addUserByAccessCode(String groupId, String userId, String accessCode) {
        Group group = groupRepository.findById(groupId).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "No group with id " + groupId + " found."));

        // check if user already exists
        if(group.getUserIdList().contains(userId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User already exists in this group!");
        }

        // check if accesskey is valid
        // System.out.println(accessCode);
        // System.out.println(group.getAccessCode());
        if (!accessCodeValid(accessCode, groupId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid access code for group " + groupId);
        }

        // add user
        group.addUserId(userId);
        groupRepository.save(group);

        // Create UserScore Object ob the coresponding user
        UserScore newUserScore = new UserScore();
        newUserScore.setUserId(userId);
        newUserScore.setGroupId(groupId);
        newUserScore.setPoints(0);
        newUserScore.setRank(1);
        newUserScore = userScoreRepository.save(newUserScore);
    }
    @Transactional
    public Group removeUserFromGroup(String groupId, String userId) {
        Group group = groupRepository.findById(groupId).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "No group with id " + groupId + " found."));

        // check if user already exists
        if (!group.getUserIdList().contains(userId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User does not exist in this group!");
        }
        // remove user
        group.removeUserId(userId);
        groupRepository.save(group);

        // remove user from user scores
        userScoreRepository.deleteByUserIdAndGroupId(userId, groupId);

        return group;
    }

    public Group getGroupById(String groupId){
        return groupRepository.findById(groupId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No group with id " + groupId + " found."));
    }

    public Group updateGroup(Group groupInput, String groupId) {
        Group group = groupRepository.findById(groupId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No group with id " + groupId + " found."));
        group.setName(groupInput.getName());
        group.setDescription(groupInput.getDescription());
        return groupRepository.save(group);
    }

    public Group deleteGroup(String groupId){
        Group group = groupRepository.findById(groupId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No group with id " + groupId + " found."));
        groupRepository.delete(group);
        return null;
    }

    public Boolean isUserAdmin(String userId, String groupId){
        Group group = groupRepository.findById(groupId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No group with id " + groupId + " found."));
        if(group.getAdminIdList().contains(userId)){
            return true;
        }
        else {
            return false;
        }
    }
}