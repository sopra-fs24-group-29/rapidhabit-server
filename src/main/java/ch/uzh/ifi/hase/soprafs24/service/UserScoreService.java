package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.entity.Habit;
import ch.uzh.ifi.hase.soprafs24.entity.UserScore;
import ch.uzh.ifi.hase.soprafs24.repository.UserScoreRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.util.List;

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

    public UserScore getUserScoreObjekt(String userId, String groupId){
        UserScore userScore = userScoreRepository.findByUserIdAndGroupId(userId, groupId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                "No such user score was found"));
        return userScore;
    }

    public void updateRanksInGroup(String groupId) {
        List<UserScore> scores = userScoreRepository.findAllByGroupIdOrderByPointsDesc(groupId);
        int currentRank = 1; // Initial rank
        int sameScores = 1; // Counter for users with the same score

        if (!scores.isEmpty()) {
            UserScore previousScore = scores.get(0);
            previousScore.setRank(currentRank); // Set rank for the highest score

            for (int i = 1; i < scores.size(); i++) {
                UserScore currentScore = scores.get(i);
                if (currentScore.getPoints() == previousScore.getPoints()) {
                    currentScore.setRank(currentRank); // Same rank for tie points
                    sameScores++; // Increment tie counter
                } else {
                    currentRank += sameScores; // Increment rank by number of ties
                    sameScores = 1; // Reset tie counter
                    currentScore.setRank(currentRank); // Set new rank
                }
                previousScore = currentScore;
            }

            // Optionally, save the updated ranks if ranks are to be persisted
            userScoreRepository.saveAll(scores);
        }
    }

    @Transactional
    public void updatePoints(String userId, String groupId, Habit habit) {
        UserScore userScore = userScoreRepository.findByUserIdAndGroupId(userId, groupId).orElseThrow(()-> new RuntimeException("No entries found"));
        if (userScore != null) {
            userScore.setPoints(userScore.getPoints() + habit.getRewardPoints()); // Add reward points to existing points
            userScoreRepository.save(userScore); // Save the updated UserScore
        } else {
            // Handle the case where the user score does not exist
            throw new IllegalStateException("UserScore not found for user ID: " + userId + " and group ID: " + groupId);
        }
    }


}