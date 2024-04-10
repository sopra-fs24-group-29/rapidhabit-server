package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.entity.Group;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.repository.GroupRepository;
// import ch.uzh.ifi.hase.soprafs24.rest.dto.UserPostDTO; // wird verwendet um die User Daten aus der intern verwendeten DTO Representation zu lesen
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
    private final BCryptPasswordEncoder encoder;

    @Autowired
    public GroupService(GroupRepository groupRepository, BCryptPasswordEncoder encoder) {
        this.groupRepository = groupRepository;
        this.encoder = encoder;
    }

    public List<Group> getGroups() {
        return this.groupRepository.findAll();
    }

    public Group createGroup(Group newGroup, String creatorId) {
        newGroup.setAccessCode(this.generateAccessCode());
        newGroup.setAdminIdList(new ArrayList<String>());
        newGroup.setUserIdList(new ArrayList<String>());
        newGroup.setHabitIdList(new ArrayList<String>());
        newGroup.addAdminId(creatorId);
        newGroup.addUserId(creatorId);
        newGroup = groupRepository.save(newGroup);
        log.debug("Created Information for User: {}", newGroup);
        return newGroup;
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

    public Group getGroupById(String groupId){
        return groupRepository.findById(groupId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No group with id " + groupId + " found."));
    }

    public Group updateGroup(Group groupInput, String groupId) {
        Group group = groupRepository.findById(groupId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No group with id " + groupId + " found."));
        group.setName(groupInput.getName());
        group.setDescription(groupInput.getDescription());
        return groupRepository.save(group);
    }
}