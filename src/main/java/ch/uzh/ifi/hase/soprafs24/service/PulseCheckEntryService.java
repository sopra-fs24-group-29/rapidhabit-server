package ch.uzh.ifi.hase.soprafs24.service;
import ch.uzh.ifi.hase.soprafs24.constant.PulseCheckStatus;
import ch.uzh.ifi.hase.soprafs24.entity.PulseCheckEntry;
import ch.uzh.ifi.hase.soprafs24.repository.PulseCheckEntryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PulseCheckEntryService {

    private final PulseCheckEntryRepository pulseCheckEntryRepository;
    private final MongoTemplate mongoTemplate;

    @Autowired
    public PulseCheckEntryService(PulseCheckEntryRepository pulseCheckEntryRepository, MongoTemplate mongoTemplate) {
        this.pulseCheckEntryRepository = pulseCheckEntryRepository;
        this.mongoTemplate = mongoTemplate;
    }

    public void createPulseCheckEntry(String formId, String groupId, String userId, String content, LocalDateTime creationTime, LocalDateTime dueTime, PulseCheckStatus pulseCheckStatus){
        PulseCheckEntry pulseCheckEntry = new PulseCheckEntry(formId, groupId,userId, content, creationTime, dueTime, pulseCheckStatus);
        pulseCheckEntryRepository.save(pulseCheckEntry);
    }

    public List<PulseCheckEntry> findByGroupIdWithLatestEntryDate(String groupId) {
        return pulseCheckEntryRepository.findByGroupIdWithLatestEntryDate(groupId, mongoTemplate);
    }

    public void setPulseCheckEntryStatus(PulseCheckEntry entry, PulseCheckStatus status){
        entry.setStatus(status);
        pulseCheckEntryRepository.save(entry);
    }

}
