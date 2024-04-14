package ch.uzh.ifi.hase.soprafs24.repository;

import ch.uzh.ifi.hase.soprafs24.entity.Group;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository("groupRepository")
public interface GroupRepository extends MongoRepository<Group, String> {
    Optional<Group> findById(String id);
    List<Group> findByUserIdsContains(String userId);
}