package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.constant.UserStatsStatus;
import ch.uzh.ifi.hase.soprafs24.entity.UserStatsEntry;
import ch.uzh.ifi.hase.soprafs24.repository.UserStatsEntryRepository;
import ch.uzh.ifi.hase.soprafs24.rest.dto.group.GroupActivityGetDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class GroupActivityService {

    @Autowired
    private UserStatsEntryRepository userStatsEntryRepository;

    public List<GroupActivityGetDTO> getActivityData(String groupId) {
        List<UserStatsEntry> entries = userStatsEntryRepository.findAllByGroupId(groupId);
        Map<LocalDate, List<UserStatsEntry>> groupedByDate = entries.stream()
                .collect(Collectors.groupingBy(UserStatsEntry::getDueDate));

        List<GroupActivityGetDTO> activityData = new ArrayList<>();

        for (Map.Entry<LocalDate, List<UserStatsEntry>> entry : groupedByDate.entrySet()) {
            LocalDate date = entry.getKey();
            List<UserStatsEntry> dateEntries = entry.getValue();

            // Check if all users have success on this date
            boolean allSuccess = dateEntries.stream()
                    .allMatch(e -> e.getStatus() == UserStatsStatus.SUCCESS);

            GroupActivityGetDTO dto = new GroupActivityGetDTO();
            dto.setDate(date);
            dto.setSuccess(allSuccess);
            activityData.add(dto);
        }

        return activityData;
    }
}
