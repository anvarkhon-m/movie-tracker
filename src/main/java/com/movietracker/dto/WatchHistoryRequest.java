package com.movietracker.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record WatchHistoryRequest(
        @NotNull @PastOrPresent LocalDate watchedAt,
        @Size(max = 100) String platform,
        String note
) {
}
