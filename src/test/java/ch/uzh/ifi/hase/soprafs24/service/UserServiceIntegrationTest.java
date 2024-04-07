package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for the UserResource REST resource.
 *
 * @see UserService
 */
@WebAppConfiguration
@SpringBootTest
public class UserServiceIntegrationTest {

  @Qualifier("userRepository")
  @Autowired
  private UserRepository userRepository;

  @Autowired
  private UserService userService;

  @BeforeEach
  public void setup() {
    userRepository.deleteAll();
  }

  @Test
  public void createUser_validInputs_success() {
     // given
     assertNull(userRepository.findByEmail("lukas.guebeli@uzh.ch"));

     User testUser = new User();
     testUser.setEmail("lukas.guebeli@uzh.ch");
     testUser.setPassword("lukas.guebeli@uzh.ch"); // Setze ein Passwort hier
 
     // when
     User createdUser = userService.createUser(testUser);
 
     // then
     assertNotNull(createdUser.getId());
     assertEquals(testUser.getEmail(), createdUser.getEmail());
     assertEquals(UserStatus.OFFLINE, createdUser.getStatus());
  }

  @Test
  public void createUser_duplicateUsername_throwsException() {
    assertNull(userRepository.findByEmail("lukas.guebeli@uzh.ch"));

    User testUser = new User();
    testUser.setEmail("lukas.guebeli@uzh.ch");
    testUser.setPassword("lukas.guebeli@uzh.ch");
    
    // create the first test user
    User createdUser = userService.createUser(testUser);

    // attempt to create second user with same username
    User testUser2 = new User();

    // change the name but forget about the username
    testUser2.setEmail("lukas.guebeli@uzh.ch");
    testUser2.setPassword("testPassword");

    // check that an error is thrown
    assertThrows(ResponseStatusException.class, () -> userService.createUser(testUser2));
  }
}
