package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserPostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserPutDTO;
import ch.uzh.ifi.hase.soprafs24.service.UserService;
import ch.uzh.ifi.hase.soprafs24.service.AuthService;
import com.fasterxml.jackson.core.JsonProcessingException;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

/**
 * UserControllerTest
 * This is a WebMvcTest which allows to test the UserController i.e. GET/POST
 * request without actually sending them over the network.
 * This tests if the UserController works.
 */
@WebMvcTest(UserController.class)
public class UserControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private UserService userService;

  @MockBean
  private AuthService authService;

    @Test
    public void givenUsers_whenGetUsers_thenReturnJsonArray() throws Exception {
        // Given
        User user = new User();
        user.setId(1L);
        user.setUsername("Simon");
        user.setStatus(UserStatus.ONLINE);
        user.setCreationDate(LocalDateTime.parse("2024-03-05T23:31:05.781639"));
        user.setBirthdate(null);

        List<User> allUsers = Collections.singletonList(user);

        String token = "JaZAJ6m4_wh7_ClFK5jr6vvnyRA";

        // Mocking AuthService to assume the token is valid
        when(authService.isTokenValid(anyString())).thenReturn(true);

        // Simulate UserService behavior
        given(userService.getUsers()).willReturn(allUsers);

        // When & Then
        mockMvc.perform(get("/users")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].username", is("Simon")))
                .andExpect(jsonPath("$[0].creationDate", is("2024-03-05T23:31:05.781639")))
                .andExpect(jsonPath("$[0].status", is(UserStatus.ONLINE.toString())))
                .andExpect(jsonPath("$[0].birthdate", is(nullValue())));
    }

    @Test
    public void createUser_validInput_userCreated() throws Exception {
        // Given
        UserPostDTO newUser = new UserPostDTO();
        newUser.setUsername("Simon");
        newUser.setPassword("password123");

        User createdUser = new User();
        createdUser.setId(1L);
        createdUser.setUsername(newUser.getUsername());
        createdUser.setStatus(UserStatus.OFFLINE);
        createdUser.setCreationDate(LocalDateTime.parse("2024-03-05T23:31:05.781639"));

        // When UserService#createUser is called, return 'createdUser'
        given(userService.createUser(Mockito.any(User.class))).willReturn(createdUser);

        // Convert newUser into JSON string
        String newUserJson = asJsonString(newUser);

        // Execute the POST request and validate the response
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newUserJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.username", is("Simon")))
                .andExpect(jsonPath("$.creationDate", is("2024-03-05T23:31:05.781639")))
                .andExpect(jsonPath("$.status", is("OFFLINE")))
                .andExpect(jsonPath("$.birthdate", is(nullValue())));
    }

    @Test
    public void createUser_whenUsernameAlreadyExists() throws Exception {
        // given
        UserPostDTO userPostDTO = new UserPostDTO();
        userPostDTO.setUsername("Simon");
        userPostDTO.setPassword("Password123");

        // Simulate that userService throws an exception when username already exists
        Mockito.when(userService.createUser(Mockito.any(User.class))).thenThrow(new ResponseStatusException(HttpStatus.CONFLICT, "Username already exists"));

        // Convert userPostDTO into a JSON string to pass into the post request
        String userPostDTOJson = asJsonString(userPostDTO);

        // when/then -> do the request + validate the result
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userPostDTOJson))
                .andExpect(status().isConflict());
    }

    // Test for retrieving user details successfully with valid token
    @Test
    void givenUserIdAndValidToken_whenGetUserDetails_thenReturnsUser() throws Exception {
        Long userId = 1L;
        User user = new User();

        user.setId(userId);
        user.setUsername("Simon");
        user.setStatus(UserStatus.ONLINE);
        user.setCreationDate(LocalDateTime.parse("2024-03-05T23:31:05.781639"));
        user.setBirthdate(null);

        String token = "JaZAJ6m4_wh7_ClFK5jr6vvnyRA";

        when(authService.isTokenValid(token)).thenReturn(true);
        when(userService.getUserDetails(userId)).thenReturn(user);

        mockMvc.perform(get("/users/{id}", userId)
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userId.intValue())))
                .andExpect(jsonPath("$.username", is("Simon")))
                .andExpect(jsonPath("$.creationDate", is("2024-03-05T23:31:05.781639")))
                .andExpect(jsonPath("$.status", is("ONLINE")))
                .andExpect(jsonPath("$.birthdate", is(nullValue())));
    }

    // Test for user not found with valid token
    @Test
    void givenUserIdAndValidToken_whenGetUserDetails_thenReturnsNotFound() throws Exception {
        Long userId = 1L;
        String token = "JaZAJ6m4_wh7_ClFK5jr6vvnyRA";

        when(authService.isTokenValid(token)).thenReturn(true);

        mockMvc.perform(get("/users/{id}", userId)
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void updateUser_ValidUserAndToken_UpdatesUser() throws Exception {
        // Given
        Long userId = 1L;
        String token = "JaZAJ6m4_wh7_ClFK5jr6vvnyRA";
        String usernameUpdate = "Simon";
        String birthdateUpdate = "1994-06-16";

        // Mocking a user object within the database layer
        User mockUser = new User();
        mockUser.setId(userId);
        mockUser.setUsername(usernameUpdate);
        mockUser.setBirthdate(LocalDate.of(1994, 6, 16)); // Datum anpassen, falls erforderlich
        mockUser.setStatus(UserStatus.ONLINE); // oder anderer Status, falls benötigt
        mockUser.setCreationDate(LocalDateTime.now());

        // Service Layer Mocks
        when(authService.isTokenValid(token)).thenReturn(true);
        when(authService.getUsername(token)).thenReturn(usernameUpdate);
        when(userService.getUserDetails(userId)).thenReturn(mockUser); // Stelle sicher, dass ein User-Objekt zurückgegeben wird

        // Write update information into JSON file for PUT request
        String updateUserJson = "{\"username\":\"" + usernameUpdate + "\",\"birthdate\":\"" + birthdateUpdate + "\"}";

        // When & Then
        mockMvc.perform(put("/users/{id}", userId)
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateUserJson))
                .andExpect(status().isNoContent()); // Status Code 204 is expected.
    }



    @Test
    public void updateUser_InvalidUserId_ReturnsNotFound() throws Exception {
        // Given
        Long invalidUserId = 2L;
        String token = "JaZAJ6m4_wh7_ClFK5jr6vvnyRA";
        String usernameUpdate = "SimonUpdate";
        String birthdateUpdate = "1990-01-01";

        // Writing update information into JSON
        String updateUserJson = "{\"username\":\"" + usernameUpdate + "\",\"birthdate\":\"" + birthdateUpdate + "\"}";

        // Service Layer Mocks
        when(authService.isTokenValid(token)).thenReturn(true);
        when(authService.getUsername(token)).thenReturn(usernameUpdate);
        when(userService.getUserDetails(invalidUserId)).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "No user with id " + invalidUserId + " found."));

        // When & Then
        mockMvc.perform(put("/users/{id}", invalidUserId)
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateUserJson))
                .andExpect(status().isNotFound()); // Expects Status COde 404.
    }



    /**
   * Helper Method to convert userPostDTO into a JSON string such that the input
   * can be processed
   * Input will look like this: {"name": "Test User", "username": "testUsername"}
   *
   * @param object
   * @return string
   */
  private String asJsonString(final Object object) {
    try {
      return new ObjectMapper().writeValueAsString(object);
    } catch (JsonProcessingException e) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
          String.format("The request body could not be created.%s", e.toString()));
    }
  }
}