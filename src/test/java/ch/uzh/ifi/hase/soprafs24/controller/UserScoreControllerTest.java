package ch.uzh.ifi.hase.soprafs24.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ch.uzh.ifi.hase.soprafs24.entity.UserScore;
import ch.uzh.ifi.hase.soprafs24.service.AuthService;
import ch.uzh.ifi.hase.soprafs24.service.GroupService;
import ch.uzh.ifi.hase.soprafs24.service.UserScoreService;
import ch.uzh.ifi.hase.soprafs24.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.test.web.servlet.MockMvc;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;

@WebMvcTest(UserScoreController.class)
class UserScoreControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @MockBean
    private GroupService groupService;

    @MockBean
    private UserService userService;

    @MockBean
    private UserScoreService userScoreService;

    @Test
    void testGetGroupScores_ValidRequest_ReturnsExpectedResponse() throws Exception {
        String groupId = "123";
        String token = "JaZAJ6m4_wh7_ClFK5jr6vvnyRA";
        when(authService.isTokenValid(token)).thenReturn(true);
        String userId = "1";
        when(authService.getId(token)).thenReturn(userId);

        // Ensure groupService.getUserIdsByGroupId returns a non-empty list
        when(groupService.getUserIdsByGroupId(groupId)).thenReturn(Arrays.asList(userId));

        UserScore mockUserScore = new UserScore();
        mockUserScore.setPoints(10);
        mockUserScore.setRank(1); // Assuming rank is relevant for this test

        when(userScoreService.getUserScoreObjekt(userId, groupId)).thenReturn(mockUserScore);
        when(userService.getInitials(userId)).thenReturn("AI");

        mockMvc.perform(get("/groups/" + groupId + "/scores")
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.scores[0].userInitials").value("AI"))
                .andExpect(jsonPath("$.scores[0].points").value(10));
    }

    @Test
    void testGetGroupScores_InvalidRequest_ReturnsUnauthorized() throws Exception {
        String groupId = "123";
        String token = "JaZAJ6m4_wh7_ClFK5jr6vvnyRA";
        when(authService.isTokenValid(token)).thenReturn(false);

        mockMvc.perform(get("/groups/" + groupId + "/scores")
                        .header("Authorization", token))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testGetGroupScores_InvalidRequest_UserNotInGroup_ReturnsUnauthorized() throws Exception {
        String groupId = "123";
        String token = "JaZAJ6m4_wh7_ClFK5jr6vvnyRA";
        when(authService.isTokenValid(token)).thenReturn(true);
        String userId = "1";
        when(authService.getId(token)).thenReturn(userId);

        // Return a list that does not contain the userId to simulate the user not being part of the group
        List<String> userIDs = Arrays.asList("user2"); // Example user ID not in the group
        when(groupService.getUserIdsByGroupId(groupId)).thenReturn(userIDs);

        mockMvc.perform(get("/groups/" + groupId + "/scores")
                        .header("Authorization", token))
                .andExpect(status().isUnauthorized());
    }
}
