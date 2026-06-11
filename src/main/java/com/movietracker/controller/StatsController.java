package com.movietracker.controller;

import com.movietracker.dto.StatsResponse;
import com.movietracker.service.StatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/stats")
@RequiredArgsConstructor
public class StatsController {

    private final StatsService statsService;

    @GetMapping
    public StatsResponse stats() {
        return statsService.getStats();
    }
}
