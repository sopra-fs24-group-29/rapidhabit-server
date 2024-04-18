package ch.uzh.ifi.hase.soprafs24.service;
import ch.uzh.ifi.hase.soprafs24.entity.Group;
import ch.uzh.ifi.hase.soprafs24.entity.UserScore;
import ch.uzh.ifi.hase.soprafs24.repository.GroupRepository;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs24.repository.UserScoreRepository;
import ch.uzh.ifi.hase.soprafs24.rest.dto.group.GroupGetDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


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
    private final UserService userService;

    @Autowired
    public GroupService(GroupRepository groupRepository, BCryptPasswordEncoder encoder, UserScoreRepository userScoreRepository, UserService userService) {
        this.groupRepository = groupRepository;
        this.userScoreRepository = userScoreRepository;
        this.userService = userService;
    }

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
    }
    public String generateAccessCode() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder accessCode = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 8; i++) {
            int index = random.nextInt(characters.length());
            accessCode.append(characters.charAt(index));
        }
        return accessCode.toString();
    }

    public void addUserByAccessCode(String userId, String accessCode) {
        Group group = groupRepository.findByAccessCode(accessCode).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "No group with access " + accessCode + " found."));

        // check if user already exists
        if(group.getUserIdList().contains(userId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User already exists in this group!");
        }

        // add user
        group.addUserId(userId);
        groupRepository.save(group);

        // Create UserScore Object ob the coresponding user
        UserScore newUserScore = new UserScore();
        newUserScore.setUserId(userId);
        newUserScore.setGroupId(group.getId());
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

        // remove user from chat -- WIP

        // remove user from user scores
        userScoreRepository.deleteByUserIdAndGroupId(userId, groupId);

        return group;
    }

    public Group getGroupById(String groupId){
        return groupRepository.findById(groupId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No group with id " + groupId + " found."));
    }
    public Group getGroupByAccessCode(String accessKey){
        return groupRepository.findByAccessCode(accessKey).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No group with access key " + accessKey + " found."));
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
    public List<GroupGetDTO> getGroupMenuDataByUserId(String userId) {
        List<Group> groupList = groupRepository.findByUserIdsContains(userId);
        if (groupList.isEmpty()) {
            List<GroupGetDTO> groupGetDTOList = new ArrayList<>();
            return groupGetDTOList;
        }

        List<GroupGetDTO> groupGetDTOList = new ArrayList<>();
        for (Group group : groupList) {
            GroupGetDTO groupGetDTO = new GroupGetDTO();
            groupGetDTO.setId(group.getId());
            groupGetDTO.setName(group.getName());
            groupGetDTO.setStreaks(group.getCurrentStreak());

            UserScore userScore = userScoreRepository.findByUserIdAndGroupId(userId, group.getId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                            "No user score found for user " + userId + " in group " + group.getId()));

            groupGetDTO.setCurrentRank(userScore.getRank());

            List<String> userIds = group.getUserIdList();
            groupGetDTO.setUserIds(userIds);

            List<String> userInitialsList = new ArrayList<>(); // Initialize the list
            for (String id : userIds) {
                String userInitial = userService.getInitials(id); // Ensure userService.getInitials() is implemented properly
                userInitialsList.add(userInitial);
            }
            groupGetDTO.setUserInitials(userInitialsList);
            groupGetDTOList.add(groupGetDTO);
        }

        return groupGetDTOList;
    }
    public void incrementCurrentStreak(String groupId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("Group not found with id: " + groupId));

        group.setCurrentStreak(group.getCurrentStreak() + 1);
        groupRepository.save(group);
    }
    public void resetCurrentStreak(String groupId){
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("Group not found with id: " + groupId));
        group.setCurrentStreak(0);
        groupRepository.save(group);
    }

}