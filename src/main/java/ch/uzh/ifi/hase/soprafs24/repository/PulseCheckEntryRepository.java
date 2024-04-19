package ch.uzh.ifi.hase.soprafs24.repository;

import ch.uzh.ifi.hase.soprafs24.entity.PulseCheckEntry;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface PulseCheckEntryRepository extends MongoRepository<PulseCheckEntry, String> {
    List<PulseCheckEntry> findByFormId(String formId);
}

