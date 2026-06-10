package com.movietracker.controller;

import com.movietracker.dto.WatchHistoryRequest;
import com.movietracker.dto.WatchHistoryResponse;
import com.movietracker.service.EpisodeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/episodes")
@RequiredArgsConstructor
public class EpisodeController {

    private final EpisodeService episodeService;

    @GetMapping("/{id}/history")
    public List<WatchHistoryResponse> getHistory(@PathVariable Long id) {
        return episodeService.getHistory(id);
    }

    @PostMapping("/{id}/history")
    @ResponseStatus(HttpStatus.CREATED)
    public WatchHistoryResponse addHistory(@PathVariable Long id,
                                           @Valid @RequestBody WatchHistoryRequest request) {
        return episodeService.addHistory(id, request);
    }

    @DeleteMapping("/{id}/history/{historyId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteHistory(@PathVariable Long id, @PathVariable Long historyId) {
        episodeService.deleteHistory(id, historyId);
    }
}
