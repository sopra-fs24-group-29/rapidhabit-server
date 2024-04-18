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


}