package com.movietracker.controller;

import com.movietracker.dto.MovieFilter;
import com.movietracker.dto.MovieRequest;
import com.movietracker.dto.MovieResponse;
import com.movietracker.dto.WatchHistoryRequest;
import com.movietracker.dto.WatchHistoryResponse;
import com.movietracker.service.MovieService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/movies")
@RequiredArgsConstructor
public class MovieController {

    private final MovieService movieService;

    @GetMapping
    public Page<MovieResponse> list(@ModelAttribute MovieFilter filter,
                                    @PageableDefault(size = 20) Pageable pageable) {
        return movieService.list(filter, pageable);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MovieResponse create(@Valid @RequestBody MovieRequest request) {
        return movieService.create(request);
    }

    @GetMapping("/{id}")
    public MovieResponse get(@PathVariable Long id) {
        return movieService.get(id);
    }

    @PutMapping("/{id}")
    public MovieResponse update(@PathVariable Long id, @Valid @RequestBody MovieRequest request) {
        return movieService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        movieService.delete(id);
    }

    @PostMapping("/{id}/refresh-rating")
    public MovieResponse refreshRating(@PathVariable Long id) {
        return movieService.refreshImdbRating(id);
    }

    // --- Watch history ---

    @GetMapping("/{id}/history")
    public List<WatchHistoryResponse> getHistory(@PathVariable Long id) {
        return movieService.getHistory(id);
    }

    @PostMapping("/{id}/history")
    @ResponseStatus(HttpStatus.CREATED)
    public WatchHistoryResponse addHistory(@PathVariable Long id,
                                           @Valid @RequestBody WatchHistoryRequest request) {
        return movieService.addHistory(id, request);
    }

    @DeleteMapping("/{id}/history/{historyId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteHistory(@PathVariable Long id, @PathVariable Long historyId) {
        movieService.deleteHistory(id, historyId);
    }
}
