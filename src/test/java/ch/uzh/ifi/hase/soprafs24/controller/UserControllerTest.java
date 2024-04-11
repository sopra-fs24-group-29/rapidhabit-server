package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.rest.dto.user.UserLoginPutDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.user.UserPasswordPutDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.user.UserPostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.user.UserPutDTO;
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

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

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

    /**
     * ------------------------------ START GET TESTS ------------------------------------------------------------
     */
    @Test //Get Mapping "/users" - CODE 200 OK (Pass)
    public void GET_users_givenUsers_whenGetUsers_thenReturnJsonArray() throws Exception {
        // Given
        User user = new User();
        user.setId(String.valueOf(1L));
        user.setFirstname("Simon");
        user.setLastname("Hafner");
        user.setEmail("Simon.hafner@uzh.ch");
        user.setStatus(UserStatus.ONLINE);

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
                .andExpect(jsonPath("$[0].id", is("1")))
                .andExpect(jsonPath("$[0].firstname", is("Simon")))
                .andExpect(jsonPath("$[0].lastname", is("Hafner")))
                .andExpect(jsonPath("$[0].email", is("Simon.hafner@uzh.ch")))
                .andExpect(jsonPath("$[0].status", is(UserStatus.ONLINE.toString())));
    }

    @Test //GET Mapping "/users/ID" - CODE 200 OK (pass)
    void GET_users_givenUserIdAndValidToken_whenGetUserDetails_thenReturnsUser() throws Exception {
        Long userId = 1L;
        User user = new User();

        user.setId(String.valueOf(1L));
        user.setFirstname("Simon");
        user.setLastname("Hafner");
        user.setEmail("Simon.hafner@uzh.ch");
        user.setPassword("Password123");

        String token = "JaZAJ6m4_wh7_ClFK5jr6vvnyRA";

        when(authService.isTokenValid(token)).thenReturn(true);
        when(userService.getUserDetails(String.valueOf(userId))).thenReturn(user);

        mockMvc.perform(get("/users/{id}", userId)
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is("1")))
                .andExpect(jsonPath("$.firstname", is("Simon")))
                .andExpect(jsonPath("$.lastname", is("Hafner")))
                .andExpect(jsonPath("$.email", is("Simon.hafner@uzh.ch")));
    }

    @Test //GET Mapping "/users/ID" - CODE 404 Not Found (error)
    void GET_users_givenUserIdAndValidToken_whenGetUserDetails_thenReturnsNotFound() throws Exception {
        Long userId = 999L;
        String token = "JaZAJ6m4_wh7_ClFK5jr6vvnyRA";

        User user = new User();
        user.setId(String.valueOf(999L));

        when(authService.isTokenValid(token)).thenReturn(true);
        when(userService.getUserDetails(String.valueOf(userId))).thenThrow(new NoSuchElementException("User with id " + userId + " not found"));

        mockMvc.perform(get("/users/{id}", userId)
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    /**
     * ------------------------------ END GET TESTS ------------------------------ START POST TESTS ------------------------------
     */

    @Test //POST Mapping "/users" - CODE 204 CREATED (Pass)
    public void POST_users_createUser_validInput_userCreated() throws Exception {
        // Given
        UserPostDTO newUser = new UserPostDTO();
        newUser.setFirstname("Simon");
        newUser.setLastname("Hafner");
        newUser.setEmail("Simon.hafner@uzh.ch");
        newUser.setPassword("password123");

        User createdUser = new User();
        createdUser.setId(String.valueOf(1L));
        createdUser.setFirstname(newUser.getFirstname());
        createdUser.setEmail(newUser.getEmail());
        createdUser.setLastname(newUser.getLastname());
        createdUser.setStatus(UserStatus.OFFLINE);

        // When UserService#createUser is called, return 'createdUser'
        given(userService.createUser(Mockito.any(User.class))).willReturn(createdUser);

        // Convert newUser into JSON string
        String newUserJson = asJsonString(newUser);

        // Execute the POST request and validate the response
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newUserJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is("1")))
                .andExpect(jsonPath("$.firstname", is("Simon")))
                .andExpect(jsonPath("$.lastname", is("Hafner")))
                .andExpect(jsonPath("$.email", is("Simon.hafner@uzh.ch")))
                .andExpect(jsonPath("$.status", is("OFFLINE")));
    }

    @Test //POST Mapping "/users" - CODE 409 CONFLICT (error)
    public void POST_users_createUser_whenUsernameAlreadyExists() throws Exception {
        // given
        UserPostDTO userPostDTO = new UserPostDTO();
        userPostDTO.setFirstname("Simon");
        userPostDTO.setLastname("Hafner");
        userPostDTO.setEmail("Simon.hafner@uzh.ch");
        userPostDTO.setPassword("Password123");

        // Simulate that userService throws an exception when username already exists
        Mockito.when(userService.createUser(Mockito.any(User.class))).thenThrow(new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists"));

        // Convert userPostDTO into a JSON string to pass into the post request
        String userPostDTOJson = asJsonString(userPostDTO);

        // when/then -> do the request + validate the result
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userPostDTOJson))
                .andExpect(status().isConflict());
    }

    /**
     * ------------------------------ END POST TESTS ------------------------------ START PUT TESTS ------------------------------
     */

    @Test //PUT Mapping "/users/ID" - CODE 204 No Content (pass)
    public void PUT_usersID_updateUser_ValidUserAndToken_UpdatesUser() throws Exception {
        Long validUserId = 1L;
        String token = "JaZAJ6m4_wh7_ClFK5jr6vvnyRA";
        String firstnameUpdate = "Lukas";
        String emailUpdate = "lukas.guebeli@uzh.ch";
        String lastnameUpdate = "guebeli";

        String updateUserJson = "{\"firstname\":\"" + firstnameUpdate + "\",\"email\":\"" + emailUpdate + "\",\"lastname\":\"" + lastnameUpdate + "\"}";

        // Mocking the User object to be returned by userService.getUserDetails
        User mockUser = new User();
        mockUser.setId(String.valueOf(validUserId));
        mockUser.setEmail(emailUpdate);
        mockUser.setFirstname(firstnameUpdate);
        mockUser.setLastname(lastnameUpdate);

        // Service Layer Mocks
        when(authService.isTokenValid(token)).thenReturn(true);
        when(authService.getId(token)).thenReturn(String.valueOf(validUserId));
        when(userService.getUserDetails(String.valueOf(validUserId))).thenReturn(mockUser);
        when(userService.updateUser(anyString(), anyString(), argThat(userPutDTO ->
                userPutDTO.getFirstname().equals(firstnameUpdate) &&
                        userPutDTO.getLastname().equals(lastnameUpdate) &&
                        userPutDTO.getEmail().equals(emailUpdate)))).thenReturn(mockUser);


        // When & Then
        mockMvc.perform(put("/users/{id}", validUserId)
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateUserJson))
                .andExpect(status().isNoContent());
    }

    @Test //PUT Mapping "/users/ID" - CODE 404 Not Found (error)
    public void PUT_usersID_updateUser_InvalidUserId_ReturnsNotFound() throws Exception {
        Long invalidUserId = 2L;
        String token = "JaZAJ6m4_wh7_ClFK5jr6vvnyRA";
        String firstnameUpdate = "Lukas";
        String emailUpdate = "lukas.guebeli@uzh.ch";
        String lastnameUpdate = "guebeli";

        String updateUserJson = "{\"firstname\":\"" + firstnameUpdate + "\",\"eMail\":\"" + emailUpdate + "\",\"lastname\":\"" + lastnameUpdate + "\"}";

        when(authService.isTokenValid(token)).thenReturn(true);
        when(userService.getUserDetails(String.valueOf(invalidUserId))).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "No user with id " + invalidUserId + " found."));

        mockMvc.perform(put("/users/{id}", invalidUserId)
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateUserJson))
                .andExpect(status().isNotFound());
    }

    @Test //PUT Mapping "/users/ID" - CODE 401 Unauthorized (error)
    public void PUT_usersID_updateUser_InvalidToken_ReturnsUnauthorized() throws Exception {
        Long userId = 2L;
        String token = "JaZAJ6m4_wh7_ClFK5jr6vvnyRA";
        String firstnameUpdate = "Lukas";
        String emailUpdate = "lukas.guebeli@uzh.ch";
        String lastnameUpdate = "guebeli";

        String updateUserJson = "{\"firstname\":\"" + firstnameUpdate + "\",\"eMail\":\"" + emailUpdate + "\",\"lastname\":\"" + lastnameUpdate + "\"}";

        when(authService.isTokenValid(token)).thenReturn(false);

        mockMvc.perform(put("/users/{id}", userId)
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateUserJson))
                .andExpect(status().isUnauthorized());
    }


    @Test //PUT Mapping "/users/login" - CODE 200 OK (pass)
    public void PUT_usersLogin_valid_ReturnsToken() throws Exception {
        String email = "test@example.com";
        String password = "password";
        String token = "JaZAJ6m4_wh7_ClFK5jr6vvnyRA";

        UserLoginPutDTO userLoginPutDTO = new UserLoginPutDTO();
        userLoginPutDTO.setEmail(email);
        userLoginPutDTO.setPassword(password);

        when(authService.userLogin(email, password)).thenReturn(token);

        // When & Then
        mockMvc.perform(put("/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"" + email + "\", \"password\":\"" + password + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value(token));
    }

    @Test //PUT Mapping "/users/login" - CODE 400 BadRequest (error)
    public void PUT_usersLogin_Invalid_ReturnsNotFound() throws Exception {
        String email = "test@example.com";
        String password = "password";

        UserLoginPutDTO userLoginPutDTO = new UserLoginPutDTO();
        userLoginPutDTO.setEmail(email);
        userLoginPutDTO.setPassword(password);

        when(authService.userLogin(email, password)).thenThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unknown email or wrong password."));

        mockMvc.perform(put("/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"" + email + "\", \"password\":\"" + password + "\"}"))
                .andExpect(status().isBadRequest()); // Expecting 400 Bad Request for incorrect credentials
    }

    @Test //PUT Mapping "/users/logout" - CODE 200 OK (pass)
    public void PUT_usersLogout_valid() throws Exception {
        Long userId = 1L;
        String token = "JaZAJ6m4_wh7_ClFK5jr6vvnyRA";

        when(authService.isTokenValid(token)).thenReturn(true);
        when(authService.getId(token)).thenReturn(String.valueOf(userId));

        mockMvc.perform(put("/users/logout")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test //PUT Mapping "/users/logout" - CODE 404 NotFound (error)
    public void PUT_usersLogout_Invalid_ReturnsNotFound() throws Exception {
        String token = "JaZAJ6m4_wh7_ClFK5jr6vvnyRA";

        when(authService.isTokenValid(token)).thenReturn(true);
        when(authService.getId(token)).thenReturn(null);

        mockMvc.perform(put("/users/logout")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test //PUT Mapping "/users/ID/password" - CODE 204 NoContent (pass)
    public void PUT_usersPasswordChange_valid_ReturnsNoContent() throws Exception {

    }

    @Test //PUT Mapping "/users/ID/password" - CODE 404 NotFound (error)
    public void PUT_usersPasswordChange_Invalid_ReturnsNotFound() throws Exception {

    }

    @Test //PUT Mapping "/users/ID/password" - CODE 401 Unauthorized (error)
    public void PUT_usersPasswordChange_Invalid_ReturnsUnauthorized() throws Exception {

    }

    /**
     * ------------------------------ END PUT TESTS ------------------------------ START DELETE TESTS ------------------------------
     */

    @Test //DELETE Mapping "/users/ID" - CODE 204 No Content (pass)
    void DELETE_users_given_ValidToken_ValidUserID_ReturnsNoContent() throws Exception {
        Long userId = 1L;
        User user = new User();
        user.setId(String.valueOf(userId));
        user.setFirstname("Simon");
        user.setLastname("Hafner");
        user.setEmail("Simon.hafner@uzh.ch");
        user.setPassword("Password123");

        String token = "JaZAJ6m4_wh7_ClFK5jr6vvnyRA";
        String userIdToEdit = user.getId();

        UserPasswordPutDTO expectedDTO = new UserPasswordPutDTO();
        expectedDTO.setCurrentPassword("Password123");

        // Mocking
        when(authService.isTokenValid(token)).thenReturn(true);
        when(authService.getId(token)).thenReturn(String.valueOf(userId));
        when(userService.getUserDetails(String.valueOf(userId))).thenReturn(user);
        when(userService.delUser(userIdToEdit, expectedDTO)).thenReturn(null);

        // Perform the DELETE request
        mockMvc.perform(delete("/users/{id}", userId)
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"currentPassword\":\"Password123\"}"))
                .andExpect(status().isNoContent());
    }

    @Test //DELETE Mapping "/users/ID" - CODE 404 NotFound (error)
    public void DELETE_users_given_ValidToken_InvalidUserID_ReturnsNoContent() throws Exception {

    }

    @Test //DELETE Mapping "/users/ID" - CODE 401 Unauthorized (error)
    public void DELETE_users_given_InvalidToken_ValidUserID_ReturnsNoContent() throws Exception {

    }

    /**
     * ------------------------------ END DELETE TESTS ------------------------------------------------------------
     */

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