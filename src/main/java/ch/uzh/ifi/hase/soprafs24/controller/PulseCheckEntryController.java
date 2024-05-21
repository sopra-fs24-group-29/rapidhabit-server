package ch.uzh.ifi.hase.soprafs24.controller;
import ch.uzh.ifi.hase.soprafs24.service.PulseCheckEntryService;
import org.springframework.web.bind.annotation.*;


@RestController
public class PulseCheckEntryController {
    private final PulseCheckEntryService pulseCheckEntryService;

    PulseCheckEntryController(PulseCheckEntryService pulseCheckEntryService){
        this.pulseCheckEntryService = pulseCheckEntryService;
    }
}
