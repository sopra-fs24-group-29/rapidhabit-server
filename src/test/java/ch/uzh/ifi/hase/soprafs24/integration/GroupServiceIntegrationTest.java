package ch.uzh.ifi.hase.soprafs24.integration;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import ch.uzh.ifi.hase.soprafs24.entity.Group;
import ch.uzh.ifi.hase.soprafs24.repository.GroupRepository;
import ch.uzh.ifi.hase.soprafs24.repository.UserScoreRepository;
import ch.uzh.ifi.hase.soprafs24.service.GroupService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.Optional;

public class GroupServiceIntegrationTest {
    @Mock
    private GroupRepository groupRepository;
    @Mock
    private UserScoreRepository userScoreRepository;
    @InjectMocks
    private GroupService groupService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGroupLifecycle() {
        // We define a group object
        Group newGroup = new Group();
        newGroup.setName("Cardio Actives");
        String creatorId = "userId1";
        when(groupRepository.save(any(Group.class))).thenAnswer(invocation -> {
            Group group = invocation.getArgument(0);
            group.setId("groupId1"); // Simulate generated ID
            return group;
        });

        // In the first step, we create a group and verfy its creation
        Group createdGroup = groupService.createGroup(newGroup, creatorId);
        assertEquals("groupId1", createdGroup.getId());
        assertEquals("Cardio Actives", createdGroup.getName());
        assertTrue(createdGroup.getUserIdList().contains(creatorId));

        // At this stage, we add a new group to the user by utilizing the acces code
        String newUserId = "userId2";
        createdGroup.setAccessCode("access123");
        when(groupRepository.findByAccessCode("access123")).thenReturn(Optional.of(createdGroup));
        groupService.addUserByAccessCode(newUserId, "access123");
        assertTrue(createdGroup.getUserIdList().contains(newUserId));

        // Now, we add a new habit to the group
        when(groupRepository.findById("groupId1")).thenReturn(Optional.of(createdGroup));
        String habitId = "habitId1";
        groupService.addHabitIdToGroup(createdGroup.getId(), habitId);
        assertTrue(createdGroup.getHabitIdList().contains(habitId));

        // Finally, we retrieve the group by its id
        Group retrievedGroup = groupService.getGroupById("groupId1");
        assertEquals("groupId1", retrievedGroup.getId());
        assertTrue(retrievedGroup.getHabitIdList().contains(habitId));
        assertTrue(retrievedGroup.getUserIdList().contains(newUserId));

        // Verify interactions
        verify(groupRepository, times(3)).save(any(Group.class)); // The 3 actions: a) group was created, b) user was addedUserByAccessCode, c) addHabitIdToGroup cause the function groupRepository.save() method to be called 3 times
        verify(groupRepository, times(2)).findById("groupId1"); // The 2 actions: a) addHabitIdToGroup and b) getGroupById –> cause the groupRepo.findById() method to be called 2 times.
        verify(groupRepository, times(1)).findByAccessCode("access123"); // // findByAcessCode causes groupRepo.findByAcessCode() to be called one time.
    }
}
