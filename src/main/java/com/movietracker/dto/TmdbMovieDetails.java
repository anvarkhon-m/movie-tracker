package com.movietracker.dto;

import java.math.BigDecimal;
import java.util.List;

public record TmdbMovieDetails(
        Integer tmdbId,
        String title,
        Integer releaseYear,
        List<String> genres,
        String director,
        BigDecimal imdbRating,
        Integer durationMin,
        String posterPath,
        String posterUrl,
        String language,
        String country,
        String overview
) {
}
