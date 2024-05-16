package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.constant.FeedType;
import ch.uzh.ifi.hase.soprafs24.entity.FeedMessage;
import ch.uzh.ifi.hase.soprafs24.repository.CustomFeedMessageRepositoryImpl;
import ch.uzh.ifi.hase.soprafs24.repository.FeedMessageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FeedMessageServiceTest {

    @Mock
    private FeedMessageRepository feedMessageRepository;

    @Mock
    private CustomFeedMessageRepositoryImpl customFeedMessageRepository;

    @InjectMocks
    private FeedMessageService feedMessageService;

    private FeedMessage testFeedMessage;

    @BeforeEach
    void setUp() {
        testFeedMessage = new FeedMessage("formId","groupId", "groupName", "message", FeedType.PULSECHECK, LocalDateTime.now());
    }

    @Test
    void createFeedMessage_success() {
        when(feedMessageRepository.save(any(FeedMessage.class))).thenReturn(testFeedMessage);

        FeedMessage result = feedMessageService.createFeedMessage(testFeedMessage.getFormId(), testFeedMessage.getGroupId(), testFeedMessage.getGroupName(), testFeedMessage.getMessage(), testFeedMessage.getType(), testFeedMessage.getDateTime());

        assertEquals(testFeedMessage, result);
        verify(feedMessageRepository, times(1)).save(any(FeedMessage.class));
    }

    @Test
    void createFeedMessage_success_() {
        when(feedMessageRepository.save(any(FeedMessage.class))).thenReturn(testFeedMessage);

        FeedMessage result = feedMessageService.createFeedMessage(testFeedMessage);

        assertEquals(testFeedMessage, result);
        verify(feedMessageRepository, times(1)).save(any(FeedMessage.class));
    }

    @Test
    void findLatestFeedMessagesByGroupIds_success() {
        List<String> groupIds = Arrays.asList("groupId1", "groupId2");
        int n = 5;
        List<FeedMessage> messages = Arrays.asList(testFeedMessage);
        when(customFeedMessageRepository.findLatestFeedMessagesByGroupIds(anyList(), anyInt())).thenReturn(messages);

        List<FeedMessage> result = feedMessageService.findLatestFeedMessagesByGroupIds(groupIds, n);

        assertEquals(1, result.size());
        verify(customFeedMessageRepository, times(1)).findLatestFeedMessagesByGroupIds(anyList(), anyInt());
    }

    @Test
    void addUserSubmit_success() {
        String feedId = "feedId";
        String userId = "userId";
        Double value = 10.0;
        when(feedMessageRepository.findById(anyString())).thenReturn(Optional.of(testFeedMessage));

        feedMessageService.addUserSubmit(feedId, userId, value);

        verify(feedMessageRepository, times(1)).findById(anyString());
        verify(feedMessageRepository, times(1)).save(any(FeedMessage.class));
    }

    @Test
    void getById_success() {
        String feedId = "feedId";
        when(feedMessageRepository.findById(anyString())).thenReturn(Optional.of(testFeedMessage));

        FeedMessage result = feedMessageService.getById(feedId);

        assertEquals(testFeedMessage, result);
        verify(feedMessageRepository, times(1)).findById(anyString());
    }

    @Test
    void getLatestPulseCheckMessage_success() {
        String groupId = "groupId";
        when(feedMessageRepository.findLatestPulseCheckByGroupId(anyString())).thenReturn(Optional.of(testFeedMessage));

        FeedMessage result = feedMessageService.getLatestPulseCheckMessage(groupId);

        assertEquals(testFeedMessage, result);
        verify(feedMessageRepository, times(1)).findLatestPulseCheckByGroupId(anyString());
    }

}
