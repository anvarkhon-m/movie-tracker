package com.movietracker.dto;

public record EpisodeResponse(
        Long id,
        int seasonNo,
        int episodeNo,
        String title,
        Integer durationMin,
        String personalNote,
        boolean watched
) {
}
