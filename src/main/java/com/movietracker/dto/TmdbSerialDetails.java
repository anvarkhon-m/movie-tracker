package com.movietracker.dto;

import com.movietracker.domain.SerialStatus;

import java.math.BigDecimal;
import java.util.List;

public record TmdbSerialDetails(
        Integer tmdbId,
        String title,
        Integer releaseYear,
        List<String> genres,
        String director,
        BigDecimal imdbRating,
        Integer seasonCount,
        Integer episodeCount,
        SerialStatus serialStatus,
        String posterPath,
        String posterUrl,
        String language,
        String country,
        String overview
) {
}
