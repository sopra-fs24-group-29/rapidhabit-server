package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.constant.GroupActivityStatus;
import ch.uzh.ifi.hase.soprafs24.constant.UserStatsStatus;
import ch.uzh.ifi.hase.soprafs24.entity.UserStatsEntry;
import ch.uzh.ifi.hase.soprafs24.repository.UserStatsEntryRepository;
import ch.uzh.ifi.hase.soprafs24.rest.dto.group.GroupActivityGetDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
public class GroupActivityServiceTest {

    @Mock
    private UserStatsEntryRepository userStatsEntryRepository;

    @InjectMocks
    private GroupActivityService groupActivityService;

    @Test
    void getActivityData_success() {
        // Given
        String groupId = "testGroupId";
        LocalDate today = LocalDate.now();
        UserStatsEntry entry1 = new UserStatsEntry();
        entry1.setDueDate(today);
        entry1.setStatus(UserStatsStatus.SUCCESS);
        UserStatsEntry entry2 = new UserStatsEntry();
        entry2.setDueDate(today);
        entry2.setStatus(UserStatsStatus.OPEN);
        List<UserStatsEntry> entries = Arrays.asList(entry1, entry2);

        when(userStatsEntryRepository.findAllByGroupId(groupId)).thenReturn(entries);

        // When
        List<GroupActivityGetDTO> result = groupActivityService.getActivityData(groupId);

        // Then
        assertEquals(1, result.size()); // Assuming today's date is the only date considered
        assertEquals(GroupActivityStatus.OPEN, result.get(0).getStatus()); // Since there's an OPEN status entry
    }

    // Add more test methods here for different scenarios
}
