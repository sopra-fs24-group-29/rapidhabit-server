package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import ch.uzh.ifi.hase.soprafs24.repository.UserStatsEntryRepository;
import ch.uzh.ifi.hase.soprafs24.rest.dto.user.UserPasswordPutDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.user.UserPutDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

  @Mock
  private UserRepository userRepository;
  @Mock
  private UserStatsEntryService userStatsEntryService;
  @Mock
  private UserStatsEntryRepository userStatsEntryRepository;

  @Mock
  private BCryptPasswordEncoder passwordEncoder;

  @InjectMocks
  private UserService userService;

  private User testUser;

  @BeforeEach
  public void setup() {
    MockitoAnnotations.openMocks(this);
    //passwordEncoder = new BCryptPasswordEncoder();
    //userService = new UserService(userRepository, passwordEncoder);

    // given
    testUser = new User();
    testUser.setId(String.valueOf(1L));
    testUser.setEmail("lukas.guebeli@uzh.ch");

    // when -> any object is being save in the userRepository -> return the dummy
    // testUser
    when(userRepository.save(Mockito.any())).thenReturn(testUser);
  }

    @Test
    void createUser_validInputs_success() {
        // Given
        User newUser = new User();
        newUser.setEmail("lukas.guebeli@uzh.ch");
        newUser.setPassword("newPassword"); // Assume the service hashes this
        User savedUser = new User();
        savedUser.setId(String.valueOf(1L));
        savedUser.setEmail("lukas.guebeli@uzh.ch");
        savedUser.setStatus(UserStatus.OFFLINE); // Assuming default status is OFFLINE
        when(userRepository.save(Mockito.any(User.class))).thenReturn(savedUser);

        // When
        User createdUser = userService.createUser(newUser);

        // Then
        verify(userRepository, times(1)).save(Mockito.any(User.class));
        assertNotNull(createdUser.getId());
        assertEquals("lukas.guebeli@uzh.ch", createdUser.getEmail());
        assertEquals(UserStatus.OFFLINE, createdUser.getStatus());
    }


  @Test
  void createUser_duplicateName_throwsException() {
    // when -> setup additional mocks for UserRepository
      when(userRepository.findByEmail("lukas.guebeli@uzh.ch")).thenReturn(Optional.ofNullable(testUser));

    // then -> attempt to create second user with same user -> check that an error
    // is thrown
      assertThrows(ResponseStatusException.class, () -> userService.createUser(testUser));
  }

  @Test
  void createUser_duplicateInputs_throwsException() {

    // when -> setup additional mocks for UserRepository
      when(userRepository.findByEmail("lukas.guebeli@uzh.ch")).thenReturn(Optional.ofNullable(testUser));

    // then -> attempt to create second user with same user -> check that an error
    // is thrown
      assertThrows(ResponseStatusException.class, () -> userService.createUser(testUser));
  }

    @Test
    void authenticateUser_correctCredentials_returnsTrue() {
        // Given
        String email = "test@example.com";
        String password = "password";
        UserStatus expectedStatus = UserStatus.ONLINE;

        User testUser = new User();
        testUser.setEmail(email);
        testUser.setPassword("encodedPassword");
        testUser.setStatus(UserStatus.OFFLINE);

        // when -> setup mocks for UserRepository and PasswordEncoder
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(testUser));
        // Ensure passwordEncoder is a mock and correctly mock the matches method
        when(passwordEncoder.matches(password, testUser.getPassword())).thenReturn(true);

        // when -> call method under test
        boolean result = userService.authenticateUser(email, password);

        // then -> assertions
        assertTrue(result);
        assertEquals(expectedStatus, testUser.getStatus());
        verify(userRepository).save(testUser);
    }

    @Test //getUser method
    void getUser_validEmail_returnsUser() {
        // Given
        String email = "test@example.com";
        User testUser = new User();
        testUser.setEmail(email);

        // When
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(testUser));

        // Then
        User result = userService.getUser(email);
        assertEquals(testUser, result);
        verify(userRepository).findByEmail(email);
    }

    @Test //getUserDetails
    void getUserDetails_validId_returnsUser() {
        // Given
        String id = "1";
        User testUser = new User();
        testUser.setId(id);

        // When
        when(userRepository.findById(id)).thenReturn(Optional.of(testUser));

        // Then
        User result = userService.getUserDetails(id);
        assertEquals(testUser, result);
        verify(userRepository).findById(id);
    }

    @Test //setUserStatusToOffline
    void setUserStatusToOffline_validId_updatesStatus() {
        String id = "1";
        User testUser = new User();
        testUser.setId(id);
        testUser.setStatus(UserStatus.ONLINE);

        when(userRepository.findById(id)).thenReturn(Optional.of(testUser));

        userService.setUserStatusToOffline(id);
        assertEquals(UserStatus.OFFLINE, testUser.getStatus());
        verify(userRepository).findById(id);
        verify(userRepository).save(testUser);
    }
    @Test
    void updateUser_validInput_updatesUser() {
        String tokenId = "JaZAJ6m4_wh7_ClFK5jr6vvnyRA";
        String userIdToEdit = "1";
        UserPutDTO userPutDTO = new UserPutDTO();
        userPutDTO.setFirstname("NewFirstName");
        userPutDTO.setLastname("NewLastName");
        userPutDTO.setEmail("newemail@example.com");

        User existingUser = new User();
        existingUser.setId(userIdToEdit);
        existingUser.setFirstname("OldFirstName");
        existingUser.setLastname("OldLastName");
        existingUser.setEmail("oldemail@example.com");

        when(userRepository.findById(userIdToEdit)).thenReturn(Optional.of(existingUser));
        when(userRepository.findByEmail(userPutDTO.getEmail())).thenReturn(Optional.empty());
        when(userRepository.save(Mockito.any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User updatedUser = userService.updateUser(tokenId, userIdToEdit, userPutDTO);

        assertEquals(userPutDTO.getFirstname(), updatedUser.getFirstname());
        assertEquals(userPutDTO.getLastname(), updatedUser.getLastname());
        assertEquals(userPutDTO.getEmail(), updatedUser.getEmail());
        verify(userRepository).findById(userIdToEdit);
        verify(userRepository).findByEmail(userPutDTO.getEmail());
        verify(userRepository).save(Mockito.any(User.class));
    }
    @Test
    void updateUser_emailChangedButExists_throwsResponseStatusException() {
        String tokenId = "JaZAJ6m4_wh7_ClFK5jr6vvnyRA";
        String userIdToEdit = "1";
        UserPutDTO userPutDTO = new UserPutDTO();
        userPutDTO.setFirstname("NewFirstName");
        userPutDTO.setLastname("NewLastName");
        userPutDTO.setEmail("newemail@example.com");

        User existingUser = new User();
        existingUser.setId(userIdToEdit);
        existingUser.setFirstname("OldFirstName");
        existingUser.setLastname("OldLastName");
        existingUser.setEmail("oldemail@example.com");

        when(userRepository.findById(userIdToEdit)).thenReturn(Optional.of(existingUser));
        when(userRepository.findByEmail(userPutDTO.getEmail())).thenReturn(Optional.of(new User())); // Simulating an existing email

        assertThrows(ResponseStatusException.class, () -> userService.updateUser(tokenId, userIdToEdit, userPutDTO), "Email already used");

        verify(userRepository, times(1)).findById(userIdToEdit);
        verify(userRepository, times(1)).findByEmail(userPutDTO.getEmail());
    }

    @Test
    void delUser_validInput_deletesUser() {
        // Given
        String userIdToEdit = "1";
        UserPasswordPutDTO userPasswordPutDTO = new UserPasswordPutDTO();
        userPasswordPutDTO.setCurrentPassword("currentPassword");

        User existingUser = new User();
        existingUser.setId(userIdToEdit);
        existingUser.setPassword("hashedCurrentPassword");

        // When
        when(userRepository.findById(userIdToEdit)).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.matches(userPasswordPutDTO.getCurrentPassword(), existingUser.getPassword())).thenReturn(true);

        // Then
        userService.delUser(userIdToEdit, userPasswordPutDTO);

        // Assertions
        verify(userRepository).findById(userIdToEdit);
        verify(passwordEncoder).matches(userPasswordPutDTO.getCurrentPassword(), existingUser.getPassword());
        verify(userRepository).delete(existingUser);
    }

    @Test
    void getInitials_validId_returnsInitials() {
        // Given
        String userId = "1";
        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setFirstname("John");
        existingUser.setLastname("Doe");

        // When
        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));

        // Then
        String initials = userService.getInitials(userId);

        // Assertions
        assertEquals("JD", initials);
        verify(userRepository).findById(userId);
    }
    @Test
    void updateUserPassword_unauthorized_dueToIncorrectCurrentPassword() {
        String userIdToEdit = "1";
        UserPasswordPutDTO userPasswordPutDTO = new UserPasswordPutDTO();
        userPasswordPutDTO.setCurrentPassword("wrongPassword");
        userPasswordPutDTO.setNewPassword("newPassword456");

        User user = new User();
        user.setId(userIdToEdit);
        user.setPassword("hashedCorrectPassword");

        when(userRepository.findById(userIdToEdit)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        assertThrows(ResponseStatusException.class, () -> userService.updateUserPassword(userIdToEdit, userPasswordPutDTO));
        verify(userRepository).findById(userIdToEdit);
    }

    @Test
    void updateUserPassword_notFound() {
        String userIdToEdit = "1";
        UserPasswordPutDTO userPasswordPutDTO = new UserPasswordPutDTO();

        when(userRepository.findById(userIdToEdit)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> userService.updateUserPassword(userIdToEdit, userPasswordPutDTO));
        verify(userRepository).findById(userIdToEdit);
    }

    @Test
    void delUser_throwsUnauthorizedException_whenCurrentPasswordIsIncorrect() {
        // Arrange
        String userIdToEdit = "1";
        UserPasswordPutDTO userPasswordPutDTO = new UserPasswordPutDTO();
        userPasswordPutDTO.setCurrentPassword("wrongPassword"); // Incorrect password

        User user = new User();
        user.setId(userIdToEdit);
        user.setPassword("correctPassword"); // Correct password stored in database

        when(userRepository.findById(userIdToEdit)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false); // Simulate incorrect password match

        // Act & Assert
        assertThrows(ResponseStatusException.class, () -> userService.delUser(userIdToEdit, userPasswordPutDTO), "Expected Unauthorized Exception");
    }

}
