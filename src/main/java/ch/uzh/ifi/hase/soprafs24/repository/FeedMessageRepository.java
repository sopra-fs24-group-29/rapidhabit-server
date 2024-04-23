package ch.uzh.ifi.hase.soprafs24.repository;

import ch.uzh.ifi.hase.soprafs24.entity.FeedMessage;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface FeedMessageRepository extends MongoRepository<FeedMessage, String> {
    // ...
}
