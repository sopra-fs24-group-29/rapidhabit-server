package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private BCryptPasswordEncoder passwordEncoder;

  @InjectMocks
  private UserService userService;

  private User testUser;

  @BeforeEach
  public void setup() {
    MockitoAnnotations.openMocks(this);
    passwordEncoder = new BCryptPasswordEncoder();
    userService = new UserService(userRepository, passwordEncoder);

    // given
    testUser = new User();
    testUser.setId(1L);
    testUser.setUsername("testUsername");

    // when -> any object is being save in the userRepository -> return the dummy
    // testUser
    Mockito.when(userRepository.save(Mockito.any())).thenReturn(testUser);
  }

    @Test
    public void createUser_validInputs_success() {
        // Given
        User newUser = new User();
        newUser.setUsername("newUsername");
        newUser.setPassword("newPassword"); // Assume the service hashes this
        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setUsername("newUsername");
        savedUser.setStatus(UserStatus.OFFLINE); // Assuming default status is OFFLINE
        Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(savedUser);

        // When
        User createdUser = userService.createUser(newUser);

        // Then
        Mockito.verify(userRepository, Mockito.times(1)).save(Mockito.any(User.class));
        assertNotNull(createdUser.getId());
        assertEquals("newUsername", createdUser.getUsername());
        assertEquals(UserStatus.OFFLINE, createdUser.getStatus());
    }


  @Test
  public void createUser_duplicateName_throwsException() {
    // when -> setup additional mocks for UserRepository
      Mockito.when(userRepository.findByUsername("testUsername")).thenReturn(testUser);

    // then -> attempt to create second user with same user -> check that an error
    // is thrown
      assertThrows(ResponseStatusException.class, () -> userService.createUser(testUser));
  }

  @Test
  public void createUser_duplicateInputs_throwsException() {

    // when -> setup additional mocks for UserRepository
      Mockito.when(userRepository.findByUsername("testUsername")).thenReturn(testUser);

    // then -> attempt to create second user with same user -> check that an error
    // is thrown
      assertThrows(ResponseStatusException.class, () -> userService.createUser(testUser));
  }

}
