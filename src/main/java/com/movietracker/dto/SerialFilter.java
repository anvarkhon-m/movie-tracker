package com.movietracker.dto;

import com.movietracker.domain.WatchStatus;

import java.math.BigDecimal;

public record SerialFilter(
        String search,
        Integer releaseYearFrom,
        Integer releaseYearTo,
        Integer watchedYearFrom,
        Integer watchedYearTo,
        String genre,
        BigDecimal minRating,
        BigDecimal maxRating,
        String language,
        String country,
        WatchStatus status
) {
}
