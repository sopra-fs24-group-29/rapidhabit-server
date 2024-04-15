package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.entity.UserStatsEntry;
import ch.uzh.ifi.hase.soprafs24.service.UserStatsEntryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserStatsEntryController.class)
public class UserStatsEntryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserStatsEntryService userStatsEntryService;

    @Test
    public void testGetAllUserStatsEntries() throws Exception {
        UserStatsEntry userStatsEntry1 = new UserStatsEntry();
        UserStatsEntry userStatsEntry2 = new UserStatsEntry();
        List<UserStatsEntry> userStatsEntries = Arrays.asList(userStatsEntry1, userStatsEntry2);

        when(userStatsEntryService.findAll()).thenReturn(userStatsEntries);

        mockMvc.perform(MockMvcRequestBuilders.get("/userStatsEntries")
                        .accept(MediaType.APPLICATION_JSON));
    }

    @Test
    public void testGetUserStatsEntryById() throws Exception {
        UserStatsEntry userStatsEntry = new UserStatsEntry();
        String id = "1";

        when(userStatsEntryService.findById(id)).thenReturn(Optional.of(userStatsEntry));

        mockMvc.perform(MockMvcRequestBuilders.get("/userStatsEntries/" + id)
                        .accept(MediaType.APPLICATION_JSON));
    }

    @Test
    public void testCreateUserStatsEntry() throws Exception {
        UserStatsEntry userStatsEntry = new UserStatsEntry(); // Initialize with appropriate values

        when(userStatsEntryService.save(any(UserStatsEntry.class))).thenReturn(userStatsEntry);

        mockMvc.perform(MockMvcRequestBuilders.post("/userStatsEntries")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":\"1\", \"name\":\"Test\"}")); // Example JSON content

    }

    @Test
    public void testDeleteUserStatsEntry() throws Exception {
        String id = "1"; // Example ID

        doNothing().when(userStatsEntryService).deleteById(id);

        mockMvc.perform(MockMvcRequestBuilders.delete("/userStatsEntries/" + id)
                        .accept(MediaType.APPLICATION_JSON));
    }
}
