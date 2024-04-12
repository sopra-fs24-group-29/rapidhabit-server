package ch.uzh.ifi.hase.soprafs24.service;
import ch.uzh.ifi.hase.soprafs24.entity.Group;
import ch.uzh.ifi.hase.soprafs24.entity.GroupStatistics;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.repository.GroupRepository;
// import ch.uzh.ifi.hase.soprafs24.rest.dto.UserPostDTO; // wird verwendet um die User Daten aus der intern verwendeten DTO Representation zu lesen
import ch.uzh.ifi.hase.soprafs24.repository.GroupStatisticsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

// import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;


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
    private final GroupStatisticsRepository groupStatisticsRepository;
    private final BCryptPasswordEncoder encoder;

    @Autowired
    public GroupService(GroupRepository groupRepository, BCryptPasswordEncoder encoder, GroupStatisticsRepository groupStatisticsRepository) {
        this.groupRepository = groupRepository;
        this.encoder = encoder;
        this.groupStatisticsRepository = groupStatisticsRepository;
    }

    // Deine Methoden hier...

    public Group createGroup(Group newGroup, String creatorId) {
        newGroup.setAccessCode(this.generateAccessCode());
        newGroup.setAdminIdList(new ArrayList<>());
        newGroup.setUserIdList(new ArrayList<>());
        newGroup.setHabitIdList(new ArrayList<>());
        newGroup.addAdminId(creatorId);
        newGroup.addUserId(creatorId);
        newGroup = groupRepository.save(newGroup);
        log.debug("Created Group: {}", newGroup);

        // Initialisiere GroupStatistics für die neue Gruppe
        GroupStatistics newGroupStatistics = new GroupStatistics();
        newGroupStatistics.setGroupId(newGroup.getId());
        groupStatisticsRepository.save(newGroupStatistics);

        return newGroup;
    }

    public List<Group> getGroups() {
        return this.groupRepository.findAll();
    }

    public void addHabitToGroup(String groupId, String habitId) {
        Group group = groupRepository.findById(groupId).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "No group with id " + groupId + " found."));
        // Check if habit id is already present in the list
        if (!group.getHabitIdList().contains(habitId)) {
            group.getHabitIdList().add(habitId);
            groupRepository.save(group);
        }
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
    }

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