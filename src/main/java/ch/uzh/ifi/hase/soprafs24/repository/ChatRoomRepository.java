package ch.uzh.ifi.hase.soprafs24.repository;

import ch.uzh.ifi.hase.soprafs24.entity.ChatRoom;
import ch.uzh.ifi.hase.soprafs24.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ChatRoomRepository extends MongoRepository<ChatRoom, String > {
    Optional<ChatRoom> findByGroupId(String groupId);

}
