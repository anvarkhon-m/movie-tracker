package com.movietracker.dto;

import java.time.LocalDate;

public record WatchHistoryResponse(
        Long id,
        LocalDate watchedAt,
        String platform,
        String note
) {
}
