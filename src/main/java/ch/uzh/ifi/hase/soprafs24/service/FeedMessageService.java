package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.entity.FeedMessage;
import ch.uzh.ifi.hase.soprafs24.constant.FeedType;
import ch.uzh.ifi.hase.soprafs24.repository.CustomFeedMessageRepositoryImpl;
import ch.uzh.ifi.hase.soprafs24.repository.FeedMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class FeedMessageService {

    private final FeedMessageRepository feedMessageRepository;

    private final CustomFeedMessageRepositoryImpl customFeedMessageRepository;

    @Autowired
    public FeedMessageService(FeedMessageRepository feedMessageRepository, CustomFeedMessageRepositoryImpl customFeedMessageRepository) {
        this.feedMessageRepository = feedMessageRepository;
        this.customFeedMessageRepository = customFeedMessageRepository;
    }

    public FeedMessage createFeedMessage(String groupId, String groupName, String message, FeedType type, LocalDateTime dateTime) {
        FeedMessage newFeedMessage = new FeedMessage(groupId, groupName, message, type, dateTime);
        return feedMessageRepository.save(newFeedMessage);
    }
    public FeedMessage createFeedMessage(FeedMessage feedMessage) {
        return feedMessageRepository.save(feedMessage);
    }
    public List<FeedMessage> findLatestFeedMessagesByGroupIds(List<String> groupIds, int n) {
        return customFeedMessageRepository.findLatestFeedMessagesByGroupIds(groupIds, n);
    }

    public void addUserSubmit(String feedId, String userId, Double value){
        FeedMessage feedMessage = feedMessageRepository.findById(feedId).orElseThrow(()->new RuntimeException("No feed message with id "+feedId +" was found."));
        feedMessage.addUserSubmits(userId,value);
        feedMessageRepository.save(feedMessage);
    }
    public FeedMessage getById(String feedId){
        return feedMessageRepository.findById(feedId).orElseThrow(()->new RuntimeException("No entry with id " +feedId +" was found."));
    }
    public FeedMessage getLatestPulseCheckMessage(String groupId) {
        return feedMessageRepository.findLatestPulseCheckByGroupId(groupId)
                .orElseThrow(() -> new RuntimeException("No pulse check messages found for group " + groupId));
    }
}
