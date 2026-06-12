package com.movietracker.dto;

import com.movietracker.domain.WatchStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record MovieResponse(
        Long id,
        Integer tmdbId,
        String title,
        Integer releaseYear,
        List<String> genres,
        String director,
        BigDecimal imdbRating,
        BigDecimal personalRating,
        Integer durationMin,
        String platform,
        String watchUrl,
        int watchCount,
        WatchStatus status,
        String personalNote,
        String overview,
        String posterUrl,
        String language,
        String country,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime imdbRatingUpdatedAt,
        LocalDate lastWatchedAt
) {
}
