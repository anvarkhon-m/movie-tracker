package com.movietracker.controller;

import com.movietracker.dto.TmdbMovieDetails;
import com.movietracker.dto.TmdbMovieSummary;
import com.movietracker.dto.TmdbSerialDetails;
import com.movietracker.dto.TmdbSerialSummary;
import com.movietracker.facade.TmdbFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tmdb")
@RequiredArgsConstructor
public class TmdbController {

    private final TmdbFacade tmdbFacade;

    @GetMapping("/search/movies")
    public List<TmdbMovieSummary> searchMovies(@RequestParam String query) {
        return tmdbFacade.searchMovies(query);
    }

    @GetMapping("/search/serials")
    public List<TmdbSerialSummary> searchSerials(@RequestParam String query) {
        return tmdbFacade.searchSerials(query);
    }

    @GetMapping("/movies/{tmdbId}")
    public TmdbMovieDetails getMovie(@PathVariable int tmdbId) {
        return tmdbFacade.getMovieDetails(tmdbId);
    }

    @GetMapping("/serials/{tmdbId}")
    public TmdbSerialDetails getSerial(@PathVariable int tmdbId) {
        return tmdbFacade.getSerialDetails(tmdbId);
    }
}
