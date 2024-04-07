package ch.uzh.ifi.hase.soprafs24.repository;

import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
public class UserRepositoryIntegrationTest {

  @Autowired
  private TestEntityManager entityManager;

  @Autowired
  private UserRepository userRepository;

  @Test
  public void findByName_success() {
    // given
    User user = new User();
    user.setEmail("firstname@lastname");
    user.setStatus(UserStatus.OFFLINE);
    user.setPassword("password");

    entityManager.persist(user);
    entityManager.flush();

    // when
    Optional<User> found = userRepository.findByEmail(user.getEmail());

    // then
   // assertNotNull(found.getId());
    //assertEquals(found.getfirstname(), user.getFirstname());
    //assertEquals(found.getStatus(), user.getStatus());
  }
}
