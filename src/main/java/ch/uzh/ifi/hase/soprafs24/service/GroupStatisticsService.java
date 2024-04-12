package ch.uzh.ifi.hase.soprafs24.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ch.uzh.ifi.hase.soprafs24.entity.GroupStatistics;
import ch.uzh.ifi.hase.soprafs24.repository.GroupStatisticsRepository;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

@Service
public class GroupStatisticsService {

    private final GroupStatisticsRepository groupStatisticsRepository;

    @Autowired
    public GroupStatisticsService(GroupStatisticsRepository groupStatisticsRepository) {
        this.groupStatisticsRepository = groupStatisticsRepository;
    }

    public GroupStatistics createOrUpdateGroupStatistics(GroupStatistics groupStatistics) {
        // This method saves a new document or updates an existing document if the id is present
        return groupStatisticsRepository.save(groupStatistics);
    }

    public Optional<GroupStatistics> getGroupStatisticsById(String id) {
        return groupStatisticsRepository.findById(id);
    }

    public List<GroupStatistics> getAllGroupStatistics() {
        return groupStatisticsRepository.findAll();
    }

    public void deleteGroupStatisticsById(String id) {
        groupStatisticsRepository.deleteById(id);
    }
    public GroupStatistics getGroupStatisticsByGroupId(String groupId) {
        return groupStatisticsRepository.findByGroupId(groupId)
                .orElseThrow(() -> new EntityNotFoundException("GroupStatistics not found for groupId: " + groupId));
    }


    // Additional methods can be defined here to interact with the data based on your requirements
}
