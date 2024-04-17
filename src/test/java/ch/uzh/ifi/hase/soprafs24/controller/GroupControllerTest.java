package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.constant.Weekday;
import ch.uzh.ifi.hase.soprafs24.entity.DailyRepeat;
import ch.uzh.ifi.hase.soprafs24.entity.Group;
import ch.uzh.ifi.hase.soprafs24.entity.Habit;
import ch.uzh.ifi.hase.soprafs24.entity.RepeatStrategy;
import ch.uzh.ifi.hase.soprafs24.repository.GroupRepository;
import ch.uzh.ifi.hase.soprafs24.rest.dto.group.GroupGetDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.group.GroupJoinPostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.group.GroupPostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.group.GroupPutDTO;
import ch.uzh.ifi.hase.soprafs24.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs24.service.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;


@WebMvcTest(GroupController.class)
public class GroupControllerTest {


    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @MockBean
    private UserService userService;

    @MockBean
    private UserStatsEntryService userStatsEntryService;

    @MockBean
    private HabitService habitService;

    @MockBean
    private UserScoreService userScoreService;

    @MockBean
    private GroupService groupService;

    @MockBean
    private GroupRepository groupRepository;


    /**
     * ------------------------------ START GET TESTS ------------------------------------------------------------
     */
     @Test //GET Mapping "/groups" - CODE 200 OK (Pass)
     public void GET_Groups_validInput_ValidToken_ReturnsNoContent() throws Exception {
         String token = "JaZAJ6m4_wh7_ClFK5jr6vvnyRA";
         when(authService.isTokenValid(token)).thenReturn(true);
         Long userId = 1L;
         when(authService.getId(token)).thenReturn(String.valueOf(userId));

         List<String> userList1 = Arrays.asList("User1", "user2");
         List<String> userList2 = Arrays.asList("User3", "user4");

         GroupGetDTO groupGetDTO1 = new GroupGetDTO();
         groupGetDTO1.setId("1");
         groupGetDTO1.setName("Group1");

         GroupGetDTO groupGetDTO2 = new GroupGetDTO();
         groupGetDTO2.setId("2");
         groupGetDTO2.setName("Group2");

         List<GroupGetDTO> groupGetDTOs = Arrays.asList(groupGetDTO1, groupGetDTO2);
         when(groupService.getGroupMenuDataByUserId(String.valueOf(userId))).thenReturn(groupGetDTOs);

         mockMvc.perform(get("/groups")
                         .header("Authorization", token)
                         .accept(MediaType.APPLICATION_JSON))
                 .andExpect(status().isOk())
                 .andExpect(jsonPath("$[0].id").value("1"))
                 .andExpect(jsonPath("$[0].name").value("Group1"))
                 .andExpect(jsonPath("$[1].id").value("2"))
                 .andExpect(jsonPath("$[1].name").value("Group2"));
     }

    @Test //GET Mapping "/groups" - CODE 401 Unaurthorized (Error)
    public void GET_Groups_validInput_InvalidToken_ReturnsNoContent() throws Exception {
        String token = "JaZAJ6m4_wh7_ClFK5jr6vvnyRA";
        when(authService.isTokenValid(token)).thenReturn(false);

        Group group1 = new Group();
        group1.setName("Group1");
        group1.setDescription("Description of Group1");
        Group group2 = new Group();
        group2.setName("Group2");
        group2.setDescription("Description of Group2");

        List<Group> groups = Arrays.asList(group1, group2);
        when(groupService.getGroups()).thenReturn(groups);

        mockMvc.perform(get("/groups")
                        .header("Authorization", token)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test //GET Mapping "/groups/{groupId}/ranking" - CODE 200 Ok (pass)
    public void GET_GroupRanking_validInput_ReturnsOk() throws Exception {
        String token = "validToken123";
        String groupId = "group1";
        String userId = "user1";

        when(authService.isTokenValid(token)).thenReturn(true);
        when(authService.getId(token)).thenReturn(userId);

        Group group = new Group();
        group.setUserIdList(Arrays.asList(userId));

        when(groupService.getGroupById(groupId)).thenReturn(group);

        when(userScoreService.getUserScore(anyString(), anyString())).thenReturn(1);
        when(userService.getInitials(anyString())).thenReturn("UI");

        mockMvc.perform(get("/groups/{groupId}/ranking", groupId)
                        .header("Authorization", token)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(userId))
                .andExpect(jsonPath("$[0].initials").value("UI"))
                .andExpect(jsonPath("$[0].rank").value(1));
    }

    /**
     * ------------------------------ END GET TESTS ------------------------------ START POST TESTS ------------------------------
     */

    @Test //POST Mapping "/groups" - CODE 201 CREATED (Pass)
    public void POST_createGroups_validInput_ReturnsNoContent() throws Exception {
        String token = "JaZAJ6m4_wh7_ClFK5jr6vvnyRA";
        Long userId = 1L;
        when(authService.isTokenValid(token)).thenReturn(true);
        when(authService.getId(token)).thenReturn(String.valueOf(userId));

        GroupPostDTO groupPostDTO = new GroupPostDTO();
        groupPostDTO.setName("Group1");
        groupPostDTO.setDescription("Description of Group1");

        Group group = new Group();
        group.setName("Group1");
        group.setDescription("Description of Group1");
        when(groupService.createGroup(Mockito.any(Group.class), eq(String.valueOf(userId)))).thenReturn(group);

        // Mock the repository save methods
        when(groupRepository.save(Mockito.any(Group.class))).thenReturn(group);


        mockMvc.perform(post("/groups")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(groupPostDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Group1"))
                .andExpect(jsonPath("$.description").value("Description of Group1"));
    }

    @Test //POST Mapping "/groups/{groupId}/users" - CODE 201 CREATED (Pass)
    public void POST_AddNewUserToGroup_validInput_ReturnsCreated() throws Exception {
        String token = "JaZAJ6m4_wh7_ClFK5jr6vvnyRA";
        Long userId = 1L;
        String groupId = "1";
        when(authService.isTokenValid(token)).thenReturn(true);
        when(authService.getId(token)).thenReturn(String.valueOf(userId));

        GroupJoinPostDTO groupJoinPostDTO = new GroupJoinPostDTO();
        groupJoinPostDTO.setAccessKey("accessKey123");

        doNothing().when(groupService).addUserByAccessCode(eq(String.valueOf(userId)), eq(groupJoinPostDTO.getAccessKey()));

        Habit mockHabit = new Habit();
        mockHabit.setRepeatStrategy(new DailyRepeat());

        Group mockGroup = new Group();
        mockGroup.setHabitIdList(Arrays.asList("habitId1"));
        when(groupService.getGroupById(groupId)).thenReturn(mockGroup);

        when(habitService.getHabitById("habitId1")).thenReturn(Optional.of(mockHabit));

        mockMvc.perform(post("/groups/" + groupId + "/users")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(groupJoinPostDTO)))
                .andExpect(status().isCreated());
    }

    @Test //POST Mapping "/groups/{groupId}/users" - CODE 401 Unauthorized (Error)
    public void POST_AddNewUserToGroup_InvalidInput_ReturnsCreated() throws Exception {
        String token = "JaZAJ6m4_wh7_ClFK5jr6vvnyRA";
        Long userId = 1L;
        String groupId = "1";
        when(authService.isTokenValid(token)).thenReturn(false);

        GroupJoinPostDTO groupJoinPostDTO = new GroupJoinPostDTO();
        groupJoinPostDTO.setAccessKey("accessKey123");

        doNothing().when(groupService).addUserByAccessCode(eq(String.valueOf(userId)), eq(groupJoinPostDTO.getAccessKey()));

        mockMvc.perform(post("/groups/" + groupId + "/users")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(groupJoinPostDTO)))
                .andExpect(status().isUnauthorized());
    }

    /**
     * ------------------------------ END POST TESTS ------------------------------ START PUT TESTS ------------------------------
     */

    @Test //PUT Mapping "/groups/{groupId}" - CODE 200 OK (Pass)
    public void PUT_updateGroup_validInput_ReturnsOk() throws Exception {
        String token = "JaZAJ6m4_wh7_ClFK5jr6vvnyRA";
        Long userId = 1L;
        String groupId = "1";
        when(authService.isTokenValid(token)).thenReturn(true);
        when(authService.getId(token)).thenReturn(String.valueOf(userId));

        GroupPutDTO groupPutDTO = new GroupPutDTO();
        groupPutDTO.setName("Updated Group Name");
        groupPutDTO.setDescription("Updated Group Description");

        Group groupInput = DTOMapper.INSTANCE.convertGroupPutDTOtoEntity(groupPutDTO);
        Group updatedGroup = new Group();
        updatedGroup.setName(groupInput.getName());
        updatedGroup.setDescription(groupInput.getDescription());

        when(groupService.updateGroup(eq(groupInput), eq(groupId))).thenReturn(updatedGroup);

        mockMvc.perform(put("/groups/" + groupId)
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(groupPutDTO)))
                .andExpect(status().isOk());

    }

    @Test //PUT Mapping "/groups/{groupId}" - CODE 404 NotFound (Pass)
    public void PUT_updateGroup_InvalidInput_WrongGroupID() throws Exception {
        String token = "JaZAJ6m4_wh7_ClFK5jr6vvnyRA";
        String groupId = "1";
        when(authService.isTokenValid(token)).thenReturn(true);

        GroupPutDTO groupPutDTO = new GroupPutDTO();
        groupPutDTO.setName("Updated Group Name");
        groupPutDTO.setDescription("Updated Group Description");

        when(groupService.updateGroup(any(Group.class), eq(groupId))).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "No group with id " + groupId + " found."));

        mockMvc.perform(put("/groups/" + groupId)
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(groupPutDTO)))
                .andExpect(status().isNotFound());
    }

    @Test //PUT Mapping "/groups/{groupId}" - CODE 401 Unauthorized (Error)
    public void PUT_updateGroup_InvalidInput_InvalidToken() throws Exception {
        String token = "JaZAJ6m4_wh7_ClFK5jr6vvnyRA";
        Long userId = 1L;
        String groupId = "1";
        when(authService.isTokenValid(token)).thenReturn(false);

        GroupPutDTO groupPutDTO = new GroupPutDTO();
        groupPutDTO.setName("Updated Group Name");
        groupPutDTO.setDescription("Updated Group Description");

        Group groupInput = DTOMapper.INSTANCE.convertGroupPutDTOtoEntity(groupPutDTO);
        Group updatedGroup = new Group();
        updatedGroup.setName(groupInput.getName());
        updatedGroup.setDescription(groupInput.getDescription());

        when(groupService.updateGroup(eq(groupInput), eq(groupId))).thenReturn(updatedGroup);

        mockMvc.perform(put("/groups/" + groupId)
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(groupPutDTO)))
                .andExpect(status().isUnauthorized());
    }

    /**
     * ------------------------------ END PUT TESTS ------------------------------ START DELETE TESTS ------------------------------
     */
    @Test // DELETE Mapping "/groups/{groupId}" - CODE 204 NoContent (Pass)
    public void DELETE_deleteGroup_ValidInput_Successful() throws Exception {
        String token = "JaZAJ6m4_wh7_ClFK5jr6vvnyRA";
        String groupId = "1";
        when(authService.isTokenValid(token)).thenReturn(true);
        when(authService.getId(token)).thenReturn("userId");
        when(groupService.isUserAdmin("userId", groupId)).thenReturn(true);

        when(groupService.deleteGroup(groupId)).thenReturn(null);

        mockMvc.perform(delete("/groups/" + groupId)
                        .header("Authorization", token))
                .andExpect(status().isNoContent());
    }

    @Test // DELETE Mapping "/groups/{groupId}" - CODE 404 NotFound (Error)
    public void DELETE_deleteGroup_GroupNotFound() throws Exception {
        String token = "JaZAJ6m4_wh7_ClFK5jr6vvnyRA";
        String groupId = "1";
        when(authService.isTokenValid(token)).thenReturn(true);
        when(authService.getId(token)).thenReturn("userId");
        when(groupService.isUserAdmin("userId", groupId)).thenReturn(true);

        // Simulate the scenario where the group does not exist
        when(groupService.deleteGroup(groupId)).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "No group with id " + groupId + " found."));

        mockMvc.perform(delete("/groups/" + groupId)
                        .header("Authorization", token))
                .andExpect(status().isNotFound());
    }

    @Test // DELETE Mapping "/groups/{groupId}" - CODE 401 Unauthorized (Error)
    public void DELETE_deleteGroup_UserNotAdmin() throws Exception {
        String token = "JaZAJ6m4_wh7_ClFK5jr6vvnyRA";
        String groupId = "1";
        when(authService.isTokenValid(token)).thenReturn(true);
        when(authService.getId(token)).thenReturn("userId");
        when(groupService.isUserAdmin("userId", groupId)).thenReturn(false);

        mockMvc.perform(delete("/groups/" + groupId)
                        .header("Authorization", token))
                .andExpect(status().isUnauthorized());
    }

    @Test // DELETE Mapping "/groups/{groupId}/users" - CODE 204 NoContent (Pass)
    public void DELETE_deleteUserFromGroup_ValidInput_Successful() throws Exception {
        String token = "JaZAJ6m4_wh7_ClFK5jr6vvnyRA";
        String groupId = "1";
        String userToRemoveID = "77";
        when(authService.isTokenValid(token)).thenReturn(true);
        when(authService.getId(token)).thenReturn("userId");
        when(groupService.isUserAdmin("userId", groupId)).thenReturn(true);

        // Mock the groupService.removeUserFromGroup method to simulate successful removal
        when(groupService.removeUserFromGroup(groupId, userToRemoveID)).thenReturn(new Group());

        mockMvc.perform(delete("/groups/" + groupId + "/users")
                        .header("Authorization", token)
                        .param("userToRemoveID", userToRemoveID))
                .andExpect(status().isNoContent());
    }

    @Test // DELETE Mapping "/groups/{groupId}/users" - CODE 404 NotFound (Error)
    public void DELETE_deleteUserFromGroup_UserToDeleteNotFound() throws Exception {
        String token = "JaZAJ6m4_wh7_ClFK5jr6vvnyRA";
        String groupId = "1";
        String userToRemoveID = "77";
        when(authService.isTokenValid(token)).thenReturn(true);
        when(authService.getId(token)).thenReturn("userId");
        when(groupService.isUserAdmin("userId", groupId)).thenReturn(true);

        // Simulate the scenario where the user does not exist in the group
        when(groupService.removeUserFromGroup(groupId, userToRemoveID)).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "User does not exist in this group!"));

        mockMvc.perform(delete("/groups/" + groupId + "/users")
                        .header("Authorization", token)
                        .param("userToRemoveID", userToRemoveID))
                .andExpect(status().isNotFound());
    }

    @Test // DELETE Mapping "/groups/{groupId}/users" - CODE 401 Unauthorized (Error)
    public void DELETE_deleteUserFromGroup_UserNotAdmin() throws Exception {
        String token = "JaZAJ6m4_wh7_ClFK5jr6vvnyRA";
        String groupId = "1";
        String userToRemoveID = "77";
        when(authService.isTokenValid(token)).thenReturn(true);
        when(authService.getId(token)).thenReturn("userId");
        when(groupService.isUserAdmin("userId", groupId)).thenReturn(false);

        mockMvc.perform(delete("/groups/" + groupId + "/users")
                        .header("Authorization", token)
                        .param("userToRemoveID", userToRemoveID))
                .andExpect(status().isUnauthorized());
    }

    /**
     * ------------------------------ END DELETE TESTS ------------------------------------------------------------
     */
}
