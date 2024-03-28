package ch.uzh.ifi.hase.soprafs24.repository;
import org.springframework.data.mongodb.repository.MongoRepository;
import ch.uzh.ifi.hase.soprafs24.entity.YourEntity;

public interface YourEntityRepository extends MongoRepository<YourEntity, String> {
    // Query methods go here
}
