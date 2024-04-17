package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.constant.GroupActivityStatus;
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

        LocalDate startDate = groupedByDate.isEmpty() ? LocalDate.now() : Collections.min(groupedByDate.keySet());
        LocalDate endDate = LocalDate.now();

        List<GroupActivityGetDTO> activityData = new ArrayList<>();

        for (LocalDate date = endDate; !date.isBefore(startDate); date = date.minusDays(1)) {
            List<UserStatsEntry> dateEntries = groupedByDate.getOrDefault(date, new ArrayList<>());

            GroupActivityGetDTO dto = new GroupActivityGetDTO();
            dto.setDate(date);

            if (dateEntries.isEmpty()) {
                dto.setStatus(GroupActivityStatus.INACTIVE);
            } else if (date.isEqual(LocalDate.now())) {
                // For today's date, we check wether the status is success or open.
                // There's no 'FAIL' OR 'INACTIVE' state.
                boolean allSuccess = dateEntries.stream()
                        .allMatch(e -> e.getStatus() == UserStatsStatus.SUCCESS);
                boolean anyOpen = dateEntries.stream()
                        .anyMatch(e -> e.getStatus() == UserStatsStatus.OPEN);

                if (allSuccess) {
                    dto.setStatus(GroupActivityStatus.SUCCESS);
                } else if (anyOpen) {
                    dto.setStatus(GroupActivityStatus.OPEN);
                } else {
                    dto.setStatus(GroupActivityStatus.FAIL);
                }
            } else {
                boolean allSuccess = dateEntries.stream()
                        .allMatch(e -> e.getStatus() == UserStatsStatus.SUCCESS);
                dto.setStatus(allSuccess ? GroupActivityStatus.SUCCESS : GroupActivityStatus.FAIL);
            }

            activityData.add(dto);
        }

        return activityData;
    }

}
