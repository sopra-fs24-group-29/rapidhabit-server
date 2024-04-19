package ch.uzh.ifi.hase.soprafs24.service;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.messaging.simp.SimpMessagingTemplate;

@Service
public class ChannelService {

    private final SimpMessagingTemplate template;
    private String currentChannel;  // Speichert den aktuellen Kanal

    public ChannelService(SimpMessagingTemplate template) {
        this.template = template;
    }

    public void subscribe(String channel) {
        this.currentChannel = channel;
    }

    @Scheduled(fixedDelay = 1000)
    private void sendPeriodicMessages() {
        if (currentChannel != null) {
            template.convertAndSend("/topic/" + currentChannel, "Congrats! You're connected.");
        }
    }
}

