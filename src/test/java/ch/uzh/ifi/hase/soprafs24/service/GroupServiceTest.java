package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.entity.Group;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.entity.UserScore;
import ch.uzh.ifi.hase.soprafs24.repository.GroupRepository;
import ch.uzh.ifi.hase.soprafs24.repository.UserScoreRepository;
import ch.uzh.ifi.hase.soprafs24.rest.dto.group.GroupGetDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GroupServiceTest {
    @Mock
    private GroupRepository groupRepository;
    @InjectMocks
    private GroupService groupService;
    @Mock
    private UserScoreRepository userScoreRepository;
    @Mock
    private UserStatsEntryService userStatsEntryService;
    @Mock
    private UserService userService;
    private Group testGroup;
    private UserScore testUserScore;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);

        testGroup = new Group();
        testGroup.setId("1");
        testGroup.setName("Test Group");
        testGroup.setUserIdList(new ArrayList<>(Arrays.asList("userID1", "userID2")));
        testGroup.setHabitIdList(new ArrayList<>(Arrays.asList("habitID1", "habitID2")));
        testUserScore = new UserScore();
        testUserScore.setUserId("1");
        testUserScore.setGroupId("1");
        testUserScore.setPoints(0);
        testUserScore.setRank(1);
    }

    @Test
    void createGroup_validInputs_success() {
        Group newGroup = new Group();
        newGroup.setName("New Group");
        String creatorId = "1";

        // Mock the behavior of the groupRepository and userScoreRepository
        Mockito.when(groupRepository.save(Mockito.any(Group.class))).thenAnswer(invocation -> {
            Group group = invocation.getArgument(0);
            group.setId("2"); // Assuming a new ID is generated
            return group;
        });

        Mockito.when(userScoreRepository.save(Mockito.any(UserScore.class))).thenAnswer(invocation -> {
            UserScore userScore = invocation.getArgument(0);
            userScore.setId("1"); // Assuming a new ID is generated
            return userScore;
        });

        Group createdGroup = groupService.createGroup(newGroup, creatorId);

        // Verify interactions with the repositories
        Mockito.verify(groupRepository, Mockito.times(1)).save(Mockito.any(Group.class));
        Mockito.verify(userScoreRepository, Mockito.times(1)).save(Mockito.any(UserScore.class));

        // Assertions
        assertNotNull(createdGroup.getId());
        assertEquals("New Group", createdGroup.getName());
        assertEquals(creatorId, createdGroup.getAdminIdList().get(0));
        assertEquals(creatorId, createdGroup.getUserIdList().get(0));
        assertEquals(0, createdGroup.getCurrentStreak());
    }
    @Test
    void addHabitIdToGroup_success() {
        // Prepare test data
        String groupId = "1";
        String habitId = "habit1";
        Group group = new Group();
        group.setId(groupId);
        group.setHabitIdList(new ArrayList<>());

        // Mock the behavior of groupRepository.findById
        when(groupRepository.findById(groupId)).thenReturn(Optional.of(group));

        // Call the method under test
        groupService.addHabitIdToGroup(groupId, habitId);

        // Verify interactions with the repository
        verify(groupRepository, times(1)).findById(groupId);
        verify(groupRepository, times(1)).save(group);

        // Assertions
        assertTrue(group.getHabitIdList().contains(habitId));
    }

    @Test
    void addHabitIdToGroup_groupNotFound() {
        // Prepare test data
        String groupId = "1";
        String habitId = "habit1";

        // Mock the behavior of groupRepository.findById to return an empty Optional
        when(groupRepository.findById(groupId)).thenReturn(Optional.empty());

        // Call the method under test and expect a ResponseStatusException
        assertThrows(ResponseStatusException.class, () -> groupService.addHabitIdToGroup(groupId, habitId));

        // Verify interactions with the repository
        verify(groupRepository, times(1)).findById(groupId);
    }

    @Test
    void addUserByAccessCode_success() {
        String userId = "user1";
        String accessCode = "accessCode1";
        Group group = new Group();
        group.setId("1");
        group.setUserIdList(new ArrayList<>());

        when(groupRepository.findByAccessCode(accessCode)).thenReturn(Optional.of(group));

        groupService.addUserByAccessCode(userId, accessCode);

        verify(groupRepository, times(1)).findByAccessCode(accessCode);
        verify(groupRepository, times(1)).save(group);
        Mockito.verify(userScoreRepository, Mockito.times(1)).save(Mockito.any(UserScore.class));

        assertTrue(group.getUserIdList().contains(userId));
    }

    @Test
    void addUserByAccessCode_userAlreadyExists() {
        // Prepare test data
        String userId = "user1";
        String accessCode = "accessCode1";
        Group group = new Group();
        group.setId("1");
        group.setUserIdList(new ArrayList<>(Arrays.asList(userId)));

        // Mock the behavior of groupRepository.findByAccessCode
        when(groupRepository.findByAccessCode(accessCode)).thenReturn(Optional.of(group));

        // Call the method under test and expect a ResponseStatusException
        assertThrows(ResponseStatusException.class, () -> groupService.addUserByAccessCode(userId, accessCode));

        // Verify interactions with the repositories
        verify(groupRepository, times(1)).findByAccessCode(accessCode);
    }

    @Test
    void addUserByAccessCode_groupNotFound() {
        // Prepare test data
        String userId = "user1";
        String accessCode = "accessCode1";

        // Mock the behavior of groupRepository.findByAccessCode to return an empty Optional
        when(groupRepository.findByAccessCode(accessCode)).thenReturn(Optional.empty());

        // Call the method under test and expect a ResponseStatusException
        assertThrows(ResponseStatusException.class, () -> groupService.addUserByAccessCode(userId, accessCode));

        // Verify interactions with the repositories
        verify(groupRepository, times(1)).findByAccessCode(accessCode);
    }

    @Test
    void removeUserFromGroup_success() {
        String groupId = "1";
        String userId = "user1";
        Group group = new Group();
        group.setId(groupId);
        group.setUserIdList(new ArrayList<>(Arrays.asList(userId)));

        when(groupRepository.findById(groupId)).thenReturn(Optional.of(group));

        Group updatedGroup = groupService.removeUserFromGroup(groupId, userId);

        verify(groupRepository, times(1)).findById(groupId);
        verify(groupRepository, times(1)).save(group);
        verify(userScoreRepository, times(1)).deleteByUserIdAndGroupId(userId, groupId);

        assertFalse(updatedGroup.getUserIdList().contains(userId));
    }

    @Test
    void removeUserFromGroup_userNotFound() {
        String groupId = "1";
        String userId = "user1";
        Group group = new Group();
        group.setId(groupId);
        group.setUserIdList(new ArrayList<>());

        when(groupRepository.findById(groupId)).thenReturn(Optional.of(group));

        assertThrows(ResponseStatusException.class, () -> groupService.removeUserFromGroup(groupId, userId));

        verify(groupRepository, times(1)).findById(groupId);
    }
    @Test
    void removeUserFromGroup_groupNotFound() {
        String groupId = "1";
        String userId = "user1";

        when(groupRepository.findById(groupId)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> groupService.removeUserFromGroup(groupId, userId));

        verify(groupRepository, times(1)).findById(groupId);
    }

    @Test
    void getGroupById_success() {
        String groupId = "1";
        Group group = new Group();
        group.setId(groupId);

        when(groupRepository.findById(groupId)).thenReturn(Optional.of(group));

        Group result = groupService.getGroupById(groupId);

        assertEquals(groupId, result.getId());
        verify(groupRepository, times(1)).findById(groupId);
    }
    @Test
    void getGroupById_notFound() {
        String groupId = "1";

        when(groupRepository.findById(groupId)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> groupService.getGroupById(groupId));
        verify(groupRepository, times(1)).findById(groupId);
    }

    @Test
    void getGroupByAccessCode_success() {
        String accessCode = "accessCode1";
        Group group = new Group();
        group.setAccessCode(accessCode);

        when(groupRepository.findByAccessCode(accessCode)).thenReturn(Optional.of(group));

        Group result = groupService.getGroupByAccessCode(accessCode);

        assertEquals(accessCode, result.getAccessCode());
        verify(groupRepository, times(1)).findByAccessCode(accessCode);
    }

    @Test
    void getGroupByAccessCode_notFound() {
        String accessCode = "accessCode1";

        when(groupRepository.findByAccessCode(accessCode)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> groupService.getGroupByAccessCode(accessCode));
        verify(groupRepository, times(1)).findByAccessCode(accessCode);
    }

    @Test
    void updateGroup_success() {
        String groupId = "1";
        Group groupInput = new Group();
        groupInput.setName("Updated Group");
        groupInput.setDescription("Updated Description");

        Group existingGroup = new Group();
        existingGroup.setId(groupId);

        Mockito.when(groupRepository.findById(groupId)).thenReturn(Optional.of(existingGroup));
        Mockito.when(groupRepository.save(Mockito.any(Group.class))).thenReturn(existingGroup);

        Group result = groupService.updateGroup(groupInput, groupId);

        assertEquals(groupInput.getName(), result.getName());
        assertEquals(groupInput.getDescription(), result.getDescription());
        verify(groupRepository, times(1)).findById(groupId);
        Mockito.verify(groupRepository, Mockito.times(1)).save(Mockito.any(Group.class));
    }
    @Test
    void deleteGroup_success() {
        String groupId = "1";
        Group group = new Group();
        group.setId(groupId);

        when(groupRepository.findById(groupId)).thenReturn(Optional.of(group));

        groupService.deleteGroup(groupId);

        verify(groupRepository, times(1)).findById(groupId);
        verify(groupRepository, times(1)).delete(group);
    }
    @Test
    void isUserAdmin_success() {
        String userId = "user1";
        String groupId = "1";
        Group group = new Group();
        group.setId(groupId);
        group.setAdminIdList(Arrays.asList(userId));

        when(groupRepository.findById(groupId)).thenReturn(Optional.of(group));

        boolean result = groupService.isUserAdmin(userId, groupId);

        assertTrue(result);
        verify(groupRepository, times(1)).findById(groupId);
    }

    @Test
    void isUserAdmin_notAdmin() {
        String userId = "user1";
        String groupId = "1";
        Group group = new Group();
        group.setId(groupId);
        group.setAdminIdList(Collections.emptyList());

        when(groupRepository.findById(groupId)).thenReturn(Optional.of(group));

        boolean result = groupService.isUserAdmin(userId, groupId);

        assertFalse(result);
        verify(groupRepository, times(1)).findById(groupId);
    }
    @Test
    void getGroupMenuDataByUserId_success() {
        String userId = "user1";
        String groupId = "group1";
        String userInitials = "UI";

        Group group = new Group();
        group.setId(groupId);
        group.setName("Group Name");
        group.setCurrentStreak(5);
        group.setUserIdList(Arrays.asList(userId));

        UserScore userScore = new UserScore();
        userScore.setRank(1);

        when(groupRepository.findByUserIdsContains(userId)).thenReturn(Arrays.asList(group));
        when(userScoreRepository.findByUserIdAndGroupId(userId, groupId)).thenReturn(Optional.of(userScore));
        when(userService.getInitials(userId)).thenReturn(userInitials);

        List<GroupGetDTO> result = groupService.getGroupMenuDataByUserId(userId);

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(groupId, result.get(0).getId());
        assertEquals("Group Name", result.get(0).getName());
        assertEquals(5, result.get(0).getStreaks());
        assertEquals(1, result.get(0).getCurrentRank());
        assertEquals(Arrays.asList(userId), result.get(0).getUserIds());
        assertEquals(Arrays.asList(userInitials), result.get(0).getUserInitials());

        verify(groupRepository, times(1)).findByUserIdsContains(userId);
        verify(userScoreRepository, times(1)).findByUserIdAndGroupId(userId, groupId);
        verify(userService, times(1)).getInitials(userId);
    }

    @Test
    void getGroupMenuDataByUserId_emptyList() {
        String userId = "user1";

        when(groupRepository.findByUserIdsContains(userId)).thenReturn(Collections.emptyList());

        List<GroupGetDTO> result = groupService.getGroupMenuDataByUserId(userId);

        assertTrue(result.isEmpty());

        verify(groupRepository, times(1)).findByUserIdsContains(userId);
    }
    @Test
    void incrementCurrentStreak_success() {
        String groupId = "group1";
        Group group = new Group();
        group.setId(groupId);
        group.setCurrentStreak(1);

        when(groupRepository.findById(groupId)).thenReturn(Optional.of(group));

        groupService.incrementCurrentStreak(groupId);

        assertEquals(2, group.getCurrentStreak());
        verify(groupRepository, times(1)).findById(groupId);
        verify(groupRepository, times(1)).save(group);
    }

    @Test
    void resetCurrentStreak_success() {
        String groupId = "group1";
        Group group = new Group();
        group.setId(groupId);
        group.setCurrentStreak(5);

        when(groupRepository.findById(groupId)).thenReturn(Optional.of(group));

        groupService.resetCurrentStreak(groupId);

        assertEquals(0, group.getCurrentStreak());
        verify(groupRepository, times(1)).findById(groupId);
        verify(groupRepository, times(1)).save(group);
    }

    @Test
    void testGetUserNamesByGroupId() {
        String groupId = "1";
        when(groupRepository.findById(groupId)).thenReturn(Optional.of(testGroup));

        User user1 = new User();
        user1.setId("userID1");
        user1.setFirstname("John");
        user1.setLastname("Doe");
        when(userService.getUserDetails("userID1")).thenReturn(user1);

        User user2 = new User();
        user2.setId("userID2");
        user2.setFirstname("Jane");
        user2.setLastname("Doe");
        when(userService.getUserDetails("userID2")).thenReturn(user2);

        Map<String, String> expectedUserNamesMap = new HashMap<>();
        expectedUserNamesMap.put("userID1", "John Doe");
        expectedUserNamesMap.put("userID2", "Jane Doe");

        Map<String, String> actualUserNamesMap = groupService.getUserNamesByGroupId(groupId);
        assertEquals(expectedUserNamesMap, actualUserNamesMap);
    }
    @Test
    void testGetGroupIdsByUserId() {
        String userId = "userID1";
        when(groupRepository.findByUserIdsContains(userId)).thenReturn(List.of(testGroup));

        List<String> expectedGroupIds = new ArrayList<>();
        expectedGroupIds.add("1");

        List<String> actualGroupIds = groupService.getGroupIdsByUserId(userId);
        assertEquals(expectedGroupIds, actualGroupIds);
    }

    @Test
    void testRemoveHabitFromHabitIdList() {
        String groupId = "1";
        String habitId = "habitID1";
        when(groupRepository.findById(groupId)).thenReturn(Optional.of(testGroup));

        groupService.removeHabitFromHabitIdList(groupId, habitId);

        verify(groupRepository, times(1)).save(testGroup);
        assertFalse(testGroup.getHabitIdList().contains(habitId));
    }

    @Test
    void testDeleteUserIdFromAllGroups() {
        String userId = "userID1";
        String userId2 = "userID2";

        Group testGroup = new Group();
        testGroup.setId("1");
        testGroup.setAdminIdList(new ArrayList<>(Arrays.asList(userId)));
        testGroup.setUserIdList(new ArrayList<>(Arrays.asList(userId2))); // Assuming "userID2" is not the admin
        when(groupRepository.findByAdminIdListContains(userId)).thenReturn(List.of(testGroup));
        when(groupRepository.findByUserIdsContains(userId)).thenReturn(List.of(testGroup));

        groupService.deleteUserIdFromAllGroups(userId);

        verify(groupRepository, times(1)).delete(testGroup);
        verify(groupRepository, times(1)).save(any(Group.class));
    }

    @Test
    void testGetUserIdsByGroupId_GroupExists_ReturnsUserIdList() {
        // Given
        String groupId = "123";
        List<String> userIds = Arrays.asList("user1", "user2");
        Group group = new Group(); // Assuming Group has a getUserIdList method
        group.setId(groupId);
        group.setUserIdList(userIds);

        when(groupRepository.findById(groupId)).thenReturn(Optional.of(group));

        // When
        List<String> result = groupService.getUserIdsByGroupId(groupId);

        // Then
        assertEquals(userIds, result);
        verify(groupRepository, times(1)).findById(groupId);
    }

    @Test
    void testGetUserIdsByGroupId_GroupNotFound_ThrowsResponseStatusException() {
        // Given
        String groupId = "123";

        when(groupRepository.findById(groupId)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResponseStatusException.class, () -> groupService.getUserIdsByGroupId(groupId));
        verify(groupRepository, times(1)).findById(groupId);
    }

}