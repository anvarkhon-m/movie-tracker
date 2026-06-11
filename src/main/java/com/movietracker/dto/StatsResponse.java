package com.movietracker.dto;

import com.movietracker.domain.WatchStatus;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public record StatsResponse(
        long movieCount,
        long serialCount,
        Map<WatchStatus, Long> moviesByStatus,
        Map<WatchStatus, Long> serialsByStatus,
        List<GenreStat> topGenres,
        BigDecimal averageMovieRating,
        BigDecimal averageSerialRating,
        Map<Integer, Long> moviesWatchedByYear
) {

    public record GenreStat(String genre, long count) {
    }
}
