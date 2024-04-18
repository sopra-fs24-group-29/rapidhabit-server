package ch.uzh.ifi.hase.soprafs24.controller;

import ch.uzh.ifi.hase.soprafs24.service.UserStatsEntryService;
import org.springframework.beans.factory.annotation.Autowired;
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
}