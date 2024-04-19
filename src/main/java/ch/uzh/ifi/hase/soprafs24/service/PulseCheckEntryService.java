package ch.uzh.ifi.hase.soprafs24.service;
import ch.uzh.ifi.hase.soprafs24.repository.PulseCheckEntryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PulseCheckEntryService {

    private final PulseCheckEntryRepository repository;

    @Autowired
    public PulseCheckEntryService(PulseCheckEntryRepository repository) {
        this.repository = repository;
    }

}
