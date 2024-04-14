package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.entity.UserScore;
import ch.uzh.ifi.hase.soprafs24.repository.UserScoreRepository;
import org.springframework.stereotype.Service;

@Service
public class UserScoreService {

    private final UserScoreRepository repository;

    public UserScoreService(UserScoreRepository repository) {
        this.repository = repository;
    }

    public UserScore addUserScore(UserScore userGroupStat) {
        return repository.save(userGroupStat);
    }


}