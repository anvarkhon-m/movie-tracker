package com.movietracker.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record EpisodeRequest(
        @NotNull @Min(1) Integer seasonNo,
        @NotNull @Min(1) Integer episodeNo,
        @Size(max = 255) String title,
        @Positive Integer durationMin,
        String personalNote
) {
}
