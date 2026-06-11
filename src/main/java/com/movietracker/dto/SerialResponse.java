package com.movietracker.dto;

import com.movietracker.domain.SerialStatus;
import com.movietracker.domain.WatchStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record SerialResponse(
        Long id,
        Integer tmdbId,
        String title,
        Integer releaseYear,
        List<String> genres,
        String director,
        BigDecimal imdbRating,
        BigDecimal personalRating,
        Integer seasonCount,
        Integer episodeCount,
        String platform,
        String watchUrl,
        SerialStatus serialStatus,
        WatchStatus watchStatus,
        String personalNote,
        String posterUrl,
        String language,
        String country,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime imdbRatingUpdatedAt
) {
}
