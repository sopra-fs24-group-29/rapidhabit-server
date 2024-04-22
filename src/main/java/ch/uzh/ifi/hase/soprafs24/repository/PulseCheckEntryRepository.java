package ch.uzh.ifi.hase.soprafs24.repository;

import ch.uzh.ifi.hase.soprafs24.entity.PulseCheckEntry;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.query.Criteria;

import java.util.List;

public interface PulseCheckEntryRepository extends MongoRepository<PulseCheckEntry, String> {
    List<PulseCheckEntry> findByFormId(String formId);
    List<PulseCheckEntry> findByGroupIdAndFormId(String groupId, String formId);

    default List<PulseCheckEntry> findByGroupIdWithLatestEntryDate(String groupId, MongoTemplate mongoTemplate) {
        MatchOperation matchStage = Aggregation.match(Criteria.where("groupId").is(groupId));
        Aggregation aggregation = Aggregation.newAggregation(
                matchStage,
                Aggregation.sort(Sort.Direction.DESC, "submissionTimestamp"),
                Aggregation.limit(1)
        );

        AggregationResults<PulseCheckEntry> results = mongoTemplate.aggregate(aggregation, "PulseCheckEntries", PulseCheckEntry.class);
        return results.getMappedResults();
    }
}
