package com.movietracker.domain;

import com.movietracker.exception.ResourceNotFoundException;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "episode")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Episode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "serial_id")
    private Serial serial;

    @Column(name = "season_no", nullable = false)
    private int seasonNo;

    @Column(name = "episode_no", nullable = false)
    private int episodeNo;

    private String title;

    @Column(name = "duration_min")
    private Integer durationMin;

    @Column(name = "personal_note")
    private String personalNote;

    @OneToMany(mappedBy = "episode", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<EpisodeWatchHistory> watchHistory = new ArrayList<>();

    // --- Domain logika ---

    public EpisodeWatchHistory recordWatch(LocalDate watchedAt, String platform, String note) {
        EpisodeWatchHistory entry = EpisodeWatchHistory.builder()
                .episode(this)
                .watchedAt(watchedAt)
                .platform(platform)
                .note(note)
                .build();
        watchHistory.add(entry);
        return entry;
    }

    public void removeWatchEntry(Long historyId) {
        boolean removed = watchHistory.removeIf(entry -> entry.getId().equals(historyId));
        if (!removed) {
            throw new ResourceNotFoundException("Watch history not found with id: " + historyId);
        }
    }

    public boolean isWatched() {
        return !watchHistory.isEmpty();
    }

    public void update(int seasonNo, int episodeNo, String title, Integer durationMin, String personalNote) {
        this.seasonNo = seasonNo;
        this.episodeNo = episodeNo;
        this.title = title;
        this.durationMin = durationMin;
        this.personalNote = personalNote;
    }
}
