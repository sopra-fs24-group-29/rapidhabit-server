package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.entity.UserStatsEntry;
import ch.uzh.ifi.hase.soprafs24.service.UserStatsEntryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("UserStatsEntries")
public class UserStatsEntryController {

    private final UserStatsEntryService userStatsEntryService;

    @Autowired
    public UserStatsEntryController(UserStatsEntryService userStatsEntryService) {
        this.userStatsEntryService = userStatsEntryService;
    }

    @GetMapping
    public List<UserStatsEntry> getAllUserStatsEntries() {
        return userStatsEntryService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserStatsEntry> getUserStatsEntryById(@PathVariable String id) {
        return userStatsEntryService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public UserStatsEntry createUserStatsEntry(@RequestBody UserStatsEntry userStatsEntry) {
        return userStatsEntryService.save(userStatsEntry);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUserStatsEntry(@PathVariable String id) {
        userStatsEntryService.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
