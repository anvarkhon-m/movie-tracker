package com.movietracker.dto;

import java.math.BigDecimal;

public record TmdbSerialSummary(
        Integer tmdbId,
        String title,
        Integer releaseYear,
        String posterUrl,
        BigDecimal rating,
        String overview
) {
}
