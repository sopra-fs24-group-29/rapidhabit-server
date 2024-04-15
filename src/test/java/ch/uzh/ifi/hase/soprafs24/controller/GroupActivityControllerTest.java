package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.repository.GroupRepository;
import ch.uzh.ifi.hase.soprafs24.rest.dto.group.GroupActivityGetDTO;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import ch.uzh.ifi.hase.soprafs24.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ch.uzh.ifi.hase.soprafs24.entity.*;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.*;
import static org.mockito.Mockito.when;


@WebMvcTest(GroupActivityController.class)
public class GroupActivityControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @MockBean
    private GroupService groupService;

    @MockBean
    private UserStatsEntryService userStatsEntryService;

    @MockBean
    private GroupActivityService groupActivityService;

    @MockBean
    private UserService userService;

    @MockBean
    private HabitService habitService;

    @MockBean
    private UserScoreService userScoreService;

    @MockBean
    private GroupRepository groupRepository;

    @Test
    public void GET_GroupActivity_validInput_ReturnsOk() throws Exception {
        String token = "JaZAJ6m4_wh7_ClFK5jr6vvnyRA";
        String groupId = "group1";
        String userId = "user1";

        when(authService.isTokenValid(token)).thenReturn(true);
        when(authService.getId(token)).thenReturn(userId);

        Group group = new Group();
        group.setUserIdList(Arrays.asList(userId));
        when(groupService.getGroupById(groupId)).thenReturn(group);

        GroupActivityGetDTO groupActivityGetDTO = new GroupActivityGetDTO();

        List<GroupActivityGetDTO> groupActivityGetDTOs = Arrays.asList(groupActivityGetDTO);
        when(groupActivityService.getActivityData(groupId)).thenReturn(groupActivityGetDTOs);

        mockMvc.perform(get("/groups/{groupId}/activity", groupId)
                        .header("Authorization", token)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

}