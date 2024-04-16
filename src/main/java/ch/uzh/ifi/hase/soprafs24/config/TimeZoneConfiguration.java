package ch.uzh.ifi.hase.soprafs24.config;

import java.util.TimeZone;
import javax.annotation.PostConstruct;
import org.springframework.stereotype.Component;

@Component
public class TimeZoneConfiguration {

    @PostConstruct
    public void init() {
        TimeZone.setDefault(TimeZone.getTimeZone("Europe/Zurich"));
        System.out.println("Zeitzone gesetzt auf: " + TimeZone.getDefault().getID());
    }
}
