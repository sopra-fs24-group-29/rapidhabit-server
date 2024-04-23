package ch.uzh.ifi.hase.soprafs24.repository;

import ch.uzh.ifi.hase.soprafs24.entity.FeedMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.SortOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CustomFeedMessageRepositoryImpl {

    private final MongoTemplate mongoTemplate;

    @Autowired
    public CustomFeedMessageRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public List<FeedMessage> findLatestFeedMessagesByGroupIds(List<String> groupIds, int n) {
        MatchOperation matchOperation = Aggregation.match(Criteria.where("groupId").in(groupIds));
        SortOperation sortOperation = Aggregation.sort(Sort.by(Sort.Direction.DESC, "dateTime"));
        Aggregation aggregation = Aggregation.newAggregation(
                matchOperation,
                sortOperation,
                Aggregation.limit(n)
        );

        AggregationResults<FeedMessage> results = mongoTemplate.aggregate(aggregation, "FeedMessages", FeedMessage.class);
        return results.getMappedResults();
    }
}
