package com.movietracker.domain;

import com.movietracker.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SerialTest {

    private Serial serial() {
        return Serial.builder().id(1L).title("Breaking Bad").build();
    }

    @Test
    void addEpisodeThenRejectsDuplicate() {
        Serial s = serial();
        s.addEpisode(1, 1, "Pilot", 58, null);
        assertThat(s.getEpisodes()).hasSize(1);
        assertThatThrownBy(() -> s.addEpisode(1, 1, "Dup", null, null))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void findEpisodeMissingThrows() {
        Serial s = serial();
        assertThatThrownBy(() -> s.findEpisode(99L)).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void markAsWatchedCompletes() {
        Serial s = serial();
        s.markAsWatched();
        assertThat(s.getWatchStatus()).isEqualTo(WatchStatus.COMPLETED);
    }

    @Test
    void dropRequiresWatching() {
        Serial s = serial();
        assertThatThrownBy(s::drop).isInstanceOf(IllegalStateException.class);
        s.startWatching();
        s.drop();
        assertThat(s.getWatchStatus()).isEqualTo(WatchStatus.DROPPED);
    }
}
