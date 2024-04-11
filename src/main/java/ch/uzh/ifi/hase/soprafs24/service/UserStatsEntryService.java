package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.entity.UserStatsEntry;
import ch.uzh.ifi.hase.soprafs24.repository.UserStatsEntryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class UserStatsEntryService {

    private final UserStatsEntryRepository userStatsEntryRepository;

    @Autowired
    public UserStatsEntryService(UserStatsEntryRepository userStatsEntryRepository) {
        this.userStatsEntryRepository = userStatsEntryRepository;
    }

    public List<UserStatsEntry> findAll() {
        return userStatsEntryRepository.findAll();
    }

    public Optional<UserStatsEntry> findById(String id) {
        return userStatsEntryRepository.findById(id);
    }

    public UserStatsEntry save(UserStatsEntry userStatsEntry) {
        return userStatsEntryRepository.save(userStatsEntry);
    }

    public void deleteById(String id) {
        userStatsEntryRepository.deleteById(id);
    }
}
