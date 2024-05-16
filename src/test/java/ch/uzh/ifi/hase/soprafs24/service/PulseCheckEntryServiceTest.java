package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.constant.PulseCheckStatus;
import ch.uzh.ifi.hase.soprafs24.entity.PulseCheckEntry;
import ch.uzh.ifi.hase.soprafs24.repository.PulseCheckEntryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PulseCheckEntryServiceTest {

    @Mock
    private PulseCheckEntryRepository pulseCheckEntryRepository;

    @Mock
    private MongoTemplate mongoTemplate;

    @InjectMocks
    private PulseCheckEntryService pulseCheckEntryService;

    private PulseCheckEntry testEntry;

    @BeforeEach
    void setUp() {
        testEntry = new PulseCheckEntry("formId", "groupId", "userId", "content", LocalDateTime.now(), LocalDateTime.now().plusDays(1), PulseCheckStatus.OPEN);
    }

    @Test
    void createPulseCheckEntry_success() {
        pulseCheckEntryService.createPulseCheckEntry(testEntry.getFormId(), testEntry.getGroupId(), testEntry.getUserId(), testEntry.getContent(), testEntry.getCreationTimestamp(), testEntry.getSubmissionTimestamp(), testEntry.getStatus());
        verify(pulseCheckEntryRepository, times(1)).save(any(PulseCheckEntry.class));
    }

    @Test
    void findByGroupIdWithLatestEntryDate_success() {
        List<PulseCheckEntry> entries = Arrays.asList(testEntry);
        when(pulseCheckEntryRepository.findByGroupIdWithLatestEntryDate(anyString(), any(MongoTemplate.class))).thenReturn(entries);

        List<PulseCheckEntry> result = pulseCheckEntryService.findByGroupIdWithLatestEntryDate("groupId");

        assertEquals(1, result.size());
        verify(pulseCheckEntryRepository, times(1)).findByGroupIdWithLatestEntryDate(anyString(), any(MongoTemplate.class));
    }

    @Test
    void setPulseCheckEntryStatus_success() {
        PulseCheckStatus newStatus = PulseCheckStatus.ACCEPTED;
        pulseCheckEntryService.setPulseCheckEntryStatus(testEntry, newStatus);

        assertEquals(newStatus, testEntry.getStatus());
        verify(pulseCheckEntryRepository, times(1)).save(any(PulseCheckEntry.class));
    }

    @Test
    void updateEntryByUserId_success() throws Exception {
        Double newValue = 10.0;

        // Explicitly set a future submissionTimestamp to ensure it's definitely later than the current time
        LocalDateTime futureTime = LocalDateTime.now().plusDays(2); // Adjust the duration as necessary

        testEntry = new PulseCheckEntry("formId", "groupId", "userId", "content", LocalDateTime.now(), futureTime, PulseCheckStatus.OPEN);

        testEntry.setUserId("userId");
        testEntry.setFormId("formId");

        when(pulseCheckEntryRepository.findByUserIdAndFormId(eq("userId"), eq("formId"), any(MongoTemplate.class))).thenReturn(Optional.of(testEntry));

        pulseCheckEntryService.updateEntryByUserId("userId", "formId", newValue);

        assertEquals(newValue, testEntry.getValue());
        assertEquals(PulseCheckStatus.ACCEPTED, testEntry.getStatus());
        verify(pulseCheckEntryRepository, times(1)).save(any(PulseCheckEntry.class));
    }


    @Test
    void updateEntryByUserId_failure() {
        Double newValue = 10.0;

        // Set the submissionTimestamp to a time before the current time
        LocalDateTime pastTime = LocalDateTime.now().minusDays(1);
        testEntry.setSubmissionTimestamp(pastTime);

        testEntry.setUserId("userId");
        testEntry.setFormId("formId");

        when(pulseCheckEntryRepository.findByUserIdAndFormId(eq("userId"), eq("formId"), any(MongoTemplate.class)))
                .thenReturn(Optional.of(testEntry));

        // Expect the updateEntryByUserId method to throw an Exception
        assertThrows(Exception.class, () -> pulseCheckEntryService.updateEntryByUserId("userId", "formId", newValue));
    }
}
