package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import ch.uzh.ifi.hase.soprafs24.repository.UserRepository;
// import ch.uzh.ifi.hase.soprafs24.rest.dto.UserPostDTO; // wird verwendet um die User Daten aus der intern verwendeten DTO Representation zu lesen
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserPostDTO;
import ch.uzh.ifi.hase.soprafs24.rest.dto.UserPutDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

// import org.springframework.security.crypto.password.PasswordEncoder;
import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


/**
 * User Service
 * This class is the "worker" and responsible for all functionality related to
 * the user
 * (e.g., it creates, modifies, deletes, finds). The result will be passed back
 * to the caller.
 */
@Service
@Transactional
public class UserService {

    private final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }


    public List<User> getUsers() {
        return this.userRepository.findAll();
    }

    public User createUser(User newUser) {
        checkIfUserExists(newUser);
        newUser.onPrePersist();
        newUser.setStatus(UserStatus.OFFLINE);
        String hashedPassword = passwordEncoder.encode(newUser.getPassword());
        newUser.setPassword(hashedPassword);
        // saves the given entity but data is only persisted in the database once
        // flush() is called
        newUser = userRepository.save(newUser);
        // userRepository.flush();

        log.debug("Created Information for User: {}", newUser);
        return newUser;
    }


    /**
     * This is a helper method that will check the uniqueness criteria of the
     * username and the name
     * defined in the User entity. The method will do nothing if the input is unique
     * and throw an error otherwise.
     *
     * @param userToBeCreated
     * @throws org.springframework.web.server.ResponseStatusException
     * @see User
     */
    private void checkIfUserExists(User userToBeCreated) {
        Optional<User> existingUser = userRepository.findByEmail(userToBeCreated.getEmail());
        if (existingUser.isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User with email " + userToBeCreated.getEmail() + " already exists.");
        }
    }



    public boolean authenticateUser(String email, String password) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Wrong username or password."));
        if(passwordEncoder.matches(password, user.getPassword())){
                user.setStatus(UserStatus.ONLINE);
                userRepository.save(user);
                return true;
            }
        return false;
    }

    public User getUser(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No user with email " + email + " found."));
    }


    public User getUserDetails(String id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No user with id " + id + " found."));
    }


    public void setUserStatusToOffline(String id) {
        User user = userRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No user with id " + id + " found."));
            user.setStatus(UserStatus.OFFLINE); // Assuming you have a setStatus method and UserStatus enum
            userRepository.save(user); // This persists the updated status to the database
        }
    public User updateUser(String tokenId, String userIdToEdit, UserPutDTO userPutDTO) {
        User user = userRepository.findById(userIdToEdit).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No user with id " + userIdToEdit + " found."));
        if (userPutDTO.getEmail().equals(user.getEmail())){
            user.setFirstname(userPutDTO.getFirstname());
            user.setLastname(userPutDTO.getLastname());
            return userRepository.save(user);
        }
        else {
            if(userRepository.findByEmail(userPutDTO.getEmail()).isPresent()){
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Email is already used.");
            }
            else {
                user.setFirstname(userPutDTO.getFirstname());
                user.setLastname(userPutDTO.getLastname());
                user.setEmail(userPutDTO.getEmail());
                return userRepository.save(user);
            }
        }
    }



}