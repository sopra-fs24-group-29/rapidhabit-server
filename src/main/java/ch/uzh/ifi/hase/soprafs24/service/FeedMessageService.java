package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.entity.FeedMessage;
import ch.uzh.ifi.hase.soprafs24.constant.FeedType;
import ch.uzh.ifi.hase.soprafs24.repository.FeedMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class FeedMessageService {

    private final FeedMessageRepository feedMessageRepository;

    @Autowired
    public FeedMessageService(FeedMessageRepository feedMessageRepository) {
        this.feedMessageRepository = feedMessageRepository;
    }

    public FeedMessage createFeedMessage(String groupId, String groupName, String title, String message, FeedType type, LocalDateTime dateTime) {
        FeedMessage newFeedMessage = new FeedMessage(groupId, groupName, title, message, type, dateTime);
        return feedMessageRepository.save(newFeedMessage);
    }
    public FeedMessage createFeedMessage(FeedMessage feedMessage) {
        return feedMessageRepository.save(feedMessage);
    }

    // ...
}
