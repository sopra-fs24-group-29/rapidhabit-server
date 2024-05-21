package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.constant.FeedType;
import ch.uzh.ifi.hase.soprafs24.entity.FeedMessage;
import ch.uzh.ifi.hase.soprafs24.entity.Group;
import ch.uzh.ifi.hase.soprafs24.rest.dto.feed.FeedMessagePulseCheckPutDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.group.GroupPutDTO;
import ch.uzh.ifi.hase.soprafs24.service.AuthService;
import ch.uzh.ifi.hase.soprafs24.service.FeedMessageService;
import ch.uzh.ifi.hase.soprafs24.service.GroupService;
import ch.uzh.ifi.hase.soprafs24.service.PulseCheckEntryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class FeedMessageControllerTest {

    @Mock
    private AuthService authService;

    @Mock
    private GroupService groupService;

    @Mock
    private FeedMessageService feedMessageService;

    @Mock
    private PulseCheckEntryService pulseCheckEntryService;

    @InjectMocks
    private FeedMessageController controller;

    private MockMvc mockMvc;

    private FeedMessage testFeedMessage;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void testGetSpecificGroupUsers_AuthorizedUser_ReturnsFeedMessages() throws Exception {
        String authToken = "validAuthToken";
        when(authService.isTokenValid(authToken)).thenReturn(true);
        when(authService.getId(authToken)).thenReturn("userId");
        when(groupService.getGroupIdsByUserId(anyString())).thenReturn(List.of("groupId1", "groupId2"));
        testFeedMessage = new FeedMessage("formId","groupId", "groupName", "message", FeedType.PULSECHECK, LocalDateTime.now());

        when(feedMessageService.findLatestFeedMessagesByGroupIds(anyList(), anyInt())).thenReturn(List.of(testFeedMessage));

        mockMvc.perform(get("/feed")
                        .header("Authorization", authToken))
                .andExpect(status().isOk())
                .andExpect(content().json("[{}]"));

        verify(authService, times(1)).isTokenValid(authToken);
        verify(authService, times(1)).getId(authToken);
        verify(groupService, times(1)).getGroupIdsByUserId(anyString());
        verify(feedMessageService, times(1)).findLatestFeedMessagesByGroupIds(anyList(), anyInt());
    }

    @Test
    void testGetSpecificGroupUsers_UnauthorizedUser_ReturnsUnauthorized() throws Exception {
        String authToken = "invalidToken";
        when(authService.isTokenValid(authToken)).thenReturn(false);

        mockMvc.perform(get("/feed")
                        .header("Authorization", authToken))
                .andExpect(status().isUnauthorized());
       }

    @Test
    void putPulseCheckResponse_invalidToken_returnsUnauthorized() throws Exception {
        String invalidAuthToken = "invalidToken";
        String groupId = "group1";
        FeedMessagePulseCheckPutDTO feedMessagePulseCheckPutDTO = new FeedMessagePulseCheckPutDTO(); // Initialize DTO as needed

        // Mock the AuthService to return false when checking if the token is valid
        when(authService.isTokenValid(invalidAuthToken)).thenReturn(false);

        mockMvc.perform(put("/groups/{groupId}/feed/pulsecheck", groupId)
                        .header("Authorization", invalidAuthToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(feedMessagePulseCheckPutDTO)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void putPulseCheckResponse_userNotInGroupIdList_returnsUnauthorized() throws Exception {
        String validAuthToken = "validToken"; // Use a valid token obtained from AuthService
        String groupId = "group1";
        FeedMessagePulseCheckPutDTO feedMessagePulseCheckPutDTO = new FeedMessagePulseCheckPutDTO(); // Initialize DTO as needed

        // Mock AuthService to return a valid token
        when(authService.isTokenValid(validAuthToken)).thenReturn(true);
        when(authService.getId(validAuthToken)).thenReturn("userId"); // Return a known userId

        // Mock GroupService to return a group that does not contain the userId
        Group group = new Group(); // Assuming Group class has a default constructor
        group.setUserIdList(Collections.singletonList("otherUserId")); // Add other users but not the userId we're testing
        when(groupService.getGroupById(groupId)).thenReturn(group);

        mockMvc.perform(put("/groups/{groupId}/feed/pulsecheck", groupId)
                        .header("Authorization", validAuthToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(feedMessagePulseCheckPutDTO)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void putPulseCheckResponse_successfulExecution_returnsOk() throws Exception {
        String validAuthToken = "validToken";
        String groupId = "group1";
        FeedMessagePulseCheckPutDTO feedMessagePulseCheckPutDTO = new FeedMessagePulseCheckPutDTO(); // Initialize DTO as needed

        // Mock AuthService to return true for token validation
        when(authService.isTokenValid(validAuthToken)).thenReturn(true);
        when(authService.getId(validAuthToken)).thenReturn("userId");

        // Mock GroupService to return a group containing the userId
        Group group = new Group(); // Assuming Group class has a default constructor
        group.setUserIdList(Collections.singletonList("userId"));
        when(groupService.getGroupById(groupId)).thenReturn(group);

        // Mock FeedMessageService to return a valid FeedMessage
        testFeedMessage = new FeedMessage("formId","groupId", "groupName", "message", FeedType.PULSECHECK, LocalDateTime.now());

        when(feedMessageService.getLatestPulseCheckMessage(groupId, feedMessagePulseCheckPutDTO.getFormId())).thenReturn(testFeedMessage);

        mockMvc.perform(put("/groups/{groupId}/feed/pulsecheck", groupId)
                        .header("Authorization", validAuthToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(feedMessagePulseCheckPutDTO)))
                .andExpect(status().isOk());
    }
}
