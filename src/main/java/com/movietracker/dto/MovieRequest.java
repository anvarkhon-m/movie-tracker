package com.movietracker.dto;

import com.movietracker.domain.WatchStatus;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.List;

public record MovieRequest(
        Integer tmdbId,
        @Size(max = 255) String title,
        @Min(1888) Integer releaseYear,
        List<String> genres,
        @Size(max = 255) String director,
        @DecimalMin("0.0") @DecimalMax("10.0") BigDecimal imdbRating,
        @DecimalMin("0.0") @DecimalMax("10.0") BigDecimal personalRating,
        @Positive Integer durationMin,
        @Size(max = 100) String platform,
        @Size(max = 500) String watchUrl,
        WatchStatus status,
        String personalNote,
        @Size(max = 50) String language,
        @Size(max = 100) String country
) {
}
