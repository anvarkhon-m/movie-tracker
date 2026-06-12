package com.movietracker.domain;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class EpisodeTest {

    @Test
    void recordWatchMarksWatched() {
        Episode e = Episode.builder()
                .id(1L)
                .serial(Serial.builder().id(1L).title("x").build())
                .seasonNo(1)
                .episodeNo(1)
                .build();

        assertThat(e.isWatched()).isFalse();
        e.recordWatch(LocalDate.of(2025, 1, 1), "Netflix", null);
        assertThat(e.isWatched()).isTrue();
        assertThat(e.getWatchHistory()).hasSize(1);
    }
}
