package com.movietracker.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MovieTest {

    private Movie movie() {
        return Movie.builder()
                .id(1L)
                .user(User.builder().id(7L).googleId("g").email("e@x.com").build())
                .title("Inception")
                .build();
    }

    @Test
    void markAsWatchedCompletesAndCounts() {
        Movie m = movie();
        m.markAsWatched();
        assertThat(m.getStatus()).isEqualTo(WatchStatus.COMPLETED);
        assertThat(m.getWatchCount()).isEqualTo(1);
    }

    @Test
    void recordWatchAddsHistoryIncrementsAndCompletes() {
        Movie m = movie();
        m.recordWatch(LocalDate.of(2025, 1, 1), "Netflix", "great");
        assertThat(m.getWatchHistory()).hasSize(1);
        assertThat(m.getWatchCount()).isEqualTo(1);
        assertThat(m.getStatus()).isEqualTo(WatchStatus.COMPLETED);
    }

    @Test
    void dropRequiresWatching() {
        Movie m = movie();
        assertThatThrownBy(m::drop).isInstanceOf(IllegalStateException.class);
        m.startWatching();
        m.drop();
        assertThat(m.getStatus()).isEqualTo(WatchStatus.DROPPED);
    }

    @Test
    void rateValidatesRange() {
        Movie m = movie();
        assertThatThrownBy(() -> m.rate(new BigDecimal("11"))).isInstanceOf(IllegalArgumentException.class);
        m.rate(new BigDecimal("8.5"));
        assertThat(m.getPersonalRating()).isEqualByComparingTo("8.5");
    }

    @Test
    void isHighRatedAboveThreshold() {
        Movie high = Movie.builder().title("x").imdbRating(new BigDecimal("8.5")).build();
        Movie low = Movie.builder().title("y").imdbRating(new BigDecimal("6.0")).build();
        assertThat(high.isHighRated()).isTrue();
        assertThat(low.isHighRated()).isFalse();
    }

    @Test
    void belongsToOwnerOnly() {
        Movie m = movie();
        assertThat(m.belongsTo(7L)).isTrue();
        assertThat(m.belongsTo(8L)).isFalse();
    }
}
