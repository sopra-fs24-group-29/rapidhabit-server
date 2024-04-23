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

    public FeedMessage createFeedMessage(String groupId, String groupName, String title, String message, FeedType type, LocalDateTime dateTime) {
        FeedMessage newFeedMessage = new FeedMessage(groupId, groupName, title, message, type, dateTime);
        return feedMessageRepository.save(newFeedMessage);
    }
    public FeedMessage createFeedMessage(FeedMessage feedMessage) {
        return feedMessageRepository.save(feedMessage);
    }
    public List<FeedMessage> findLatestFeedMessagesByGroupIds(List<String> groupIds, int n) {
        return customFeedMessageRepository.findLatestFeedMessagesByGroupIds(groupIds, n);
    }

}
