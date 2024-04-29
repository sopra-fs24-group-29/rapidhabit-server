package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.entity.Habit;
import ch.uzh.ifi.hase.soprafs24.entity.UserScore;
import ch.uzh.ifi.hase.soprafs24.repository.UserScoreRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserScoreServiceTest {
    @Mock
    UserScoreRepository userScoreRepository;
    @InjectMocks
    private UserScoreService userScoreService;
    @Test
    void addUserScore_success() {
        UserScore testScore = new UserScore();
        testScore.setUserId("1");
        testScore.setGroupId("2");
        testScore.setPoints(10);
        when(userScoreRepository.save(any(UserScore.class))).thenReturn(testScore);

        UserScore result = userScoreService.addUserScore(testScore);

        assertEquals(testScore.getUserId(), result.getUserId());
        assertEquals(testScore.getGroupId(), result.getGroupId());
        assertEquals(testScore.getPoints(), result.getPoints());
        verify(userScoreRepository, times(1)).save(any(UserScore.class));
    }
    @Test
    void getUserScore_success() {
        UserScore testScore = new UserScore();
        testScore.setRank(1);
        when(userScoreRepository.findByUserIdAndGroupId("1", "2")).thenReturn(Optional.of(testScore));

        int result = userScoreService.getUserScore("1", "2");

        assertEquals(1, result);
    }
    @Test
    void getUserScoreObjekt_success() {
        UserScore testScore = new UserScore();
        testScore.setRank(1);
        when(userScoreRepository.findByUserIdAndGroupId("1", "2")).thenReturn(Optional.of(testScore));

        UserScore result = userScoreService.getUserScoreObjekt("1", "2");

        assertEquals(1, result.getRank());
    }
    @Test
    void updateRanksInGroup_success() {
        UserScore score1 = new UserScore();
        score1.setPoints(10);
        UserScore score2 = new UserScore();
        score2.setPoints(10);
        UserScore score3 = new UserScore();
        score3.setPoints(5);
        List<UserScore> scores = Arrays.asList(score1, score2, score3);
        when(userScoreRepository.findAllByGroupIdOrderByPointsDesc("1")).thenReturn(scores);

        userScoreService.updateRanksInGroup("1");

        assertEquals(1, score1.getRank());
        assertEquals(1, score2.getRank());
        assertEquals(3, score3.getRank());
        verify(userScoreRepository, times(1)).saveAll(scores);
    }
    @Test
    void updatePoints_success() {
        UserScore testScore = new UserScore();
        testScore.setPoints(10);
        Habit testHabit = new Habit();
        testHabit.setRewardPoints(5);
        when(userScoreRepository.findByUserIdAndGroupId("1", "2")).thenReturn(Optional.of(testScore));

        userScoreService.updatePoints("1", "2", testHabit);

        assertEquals(15, testScore.getPoints());
        verify(userScoreRepository, times(1)).save(testScore);
    }
    @Test
    void deleteByUserIdAndGroupId_success() {
        // Given
        String userId = "1";
        String groupId = "2";

        // When
        userScoreService.deleteByUserIdAndGroupId(userId, groupId);

        // Then
        verify(userScoreRepository, times(1)).deleteByUserIdAndGroupId(userId, groupId);
    }

    @Test
    void deleteByGroupId_success() {
        // Given
        String groupId = "2";

        // When
        userScoreService.deleteByGroupId(groupId);

        // Then
        verify(userScoreRepository, times(1)).deleteByGroupId(groupId);
    }

}
