package ch.uzh.ifi.hase.soprafs24.repository;

import ch.uzh.ifi.hase.soprafs24.entity.PulseCheckEntry;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.query.Criteria;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public interface PulseCheckEntryRepository extends MongoRepository<PulseCheckEntry, String> {
    List<PulseCheckEntry> findByFormId(String formId);
    List<PulseCheckEntry> findByGroupIdAndFormId(String groupId, String formId);

    default List<PulseCheckEntry> findByGroupIdWithLatestEntryDate(String groupId, MongoTemplate mongoTemplate) {
        // Schritt 1: Ermittle das neueste Datum für die gegebene groupId
        Aggregation findMaxDateAggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("groupId").is(groupId)),
                Aggregation.group("groupId").max("submissionTimestamp").as("maxDate")
        );
        AggregationResults<MaxDateResult> maxDateResult = mongoTemplate.aggregate(findMaxDateAggregation, "PulseCheckEntries", MaxDateResult.class);
        LocalDateTime maxDate = maxDateResult.getMappedResults().isEmpty() ? null : maxDateResult.getMappedResults().get(0).getMaxDate();

        if (maxDate == null) {
            return new ArrayList<>();  // Keine Einträge gefunden, gibt leere Liste zurück
        }

        // Schritt 2: Hole alle Einträge, die zu diesem neuesten Datum gehören
        Aggregation findAllEntriesAtMaxDateAggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("groupId").is(groupId).and("submissionTimestamp").is(maxDate))
        );
        AggregationResults<PulseCheckEntry> results = mongoTemplate.aggregate(findAllEntriesAtMaxDateAggregation, "PulseCheckEntries", PulseCheckEntry.class);
        return results.getMappedResults();
    }

    // Hilfsklasse, um das Ergebnis der Aggregation für das maximale Datum zu speichern
    static class MaxDateResult {
        private LocalDateTime maxDate;

        public LocalDateTime getMaxDate() {
            return maxDate;
        }

        public void setMaxDate(LocalDateTime maxDate) {
            this.maxDate = maxDate;
        }
    }

}
