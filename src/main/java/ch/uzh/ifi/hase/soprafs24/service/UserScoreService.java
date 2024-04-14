package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.entity.UserScore;
import ch.uzh.ifi.hase.soprafs24.repository.UserScoreRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UserScoreService {

    private final UserScoreRepository userScoreRepository;

    public UserScoreService(UserScoreRepository repository) {
        this.userScoreRepository = repository;
    }

    public UserScore addUserScore(UserScore userGroupStat) {
        return userScoreRepository.save(userGroupStat);
    }

    public int getUserScore(String userId, String groupId){
        UserScore userScore = userScoreRepository.findByUserIdAndGroupId(userId, groupId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                "No such user score was found"));
        int userRank = userScore.getRank();
        return userRank;
    }


}