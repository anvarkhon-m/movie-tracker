package com.movietracker.domain;

import com.movietracker.exception.ResourceNotFoundException;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "serial")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Serial {

    private static final BigDecimal HIGH_RATING_THRESHOLD = new BigDecimal("8.0");
    private static final BigDecimal MIN_RATING = BigDecimal.ZERO;
    private static final BigDecimal MAX_RATING = BigDecimal.TEN;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "tmdb_id")
    private Integer tmdbId;

    @Column(nullable = false)
    private String title;

    @Column(name = "release_year")
    private Integer releaseYear;

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(columnDefinition = "text[]")
    @Builder.Default
    private List<String> genres = new ArrayList<>();

    private String director;

    @Column(name = "imdb_rating", precision = 3, scale = 1)
    private BigDecimal imdbRating;

    @Column(name = "personal_rating", precision = 3, scale = 1)
    private BigDecimal personalRating;

    @Column(name = "season_count")
    private Integer seasonCount;

    @Column(name = "episode_count")
    private Integer episodeCount;

    private String platform;

    @Column(name = "watch_url")
    private String watchUrl;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "serial_status", columnDefinition = "serial_status")
    @Builder.Default
    private SerialStatus serialStatus = SerialStatus.ONGOING;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "watch_status", columnDefinition = "watch_status", nullable = false)
    @Builder.Default
    private WatchStatus watchStatus = WatchStatus.PLAN_TO_WATCH;

    @Column(name = "personal_note")
    private String personalNote;

    @Column(name = "poster_url")
    private String posterUrl;

    private String language;

    private String country;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "serial", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Episode> episodes = new ArrayList<>();

    // --- Domain logika ---

    public void markAsWatched() {
        this.watchStatus = WatchStatus.COMPLETED;
    }

    public void startWatching() {
        this.watchStatus = WatchStatus.WATCHING;
    }

    public boolean canBeDropped() {
        return watchStatus == WatchStatus.WATCHING;
    }

    public void drop() {
        if (!canBeDropped()) {
            throw new IllegalStateException("Faqat WATCHING holatdagi serial DROPPED qilinadi");
        }
        this.watchStatus = WatchStatus.DROPPED;
    }

    public boolean isHighRated() {
        return imdbRating != null && imdbRating.compareTo(HIGH_RATING_THRESHOLD) > 0;
    }

    public void rate(BigDecimal rating) {
        validateRating(rating);
        this.personalRating = rating;
    }

    public Episode addEpisode(int seasonNo, int episodeNo, String title, Integer durationMin, String personalNote) {
        boolean exists = episodes.stream()
                .anyMatch(e -> e.getSeasonNo() == seasonNo && e.getEpisodeNo() == episodeNo);
        if (exists) {
            throw new IllegalStateException(
                    "Epizod allaqachon mavjud: S%02dE%02d".formatted(seasonNo, episodeNo));
        }
        Episode episode = Episode.builder()
                .serial(this)
                .seasonNo(seasonNo)
                .episodeNo(episodeNo)
                .title(title)
                .durationMin(durationMin)
                .personalNote(personalNote)
                .build();
        episodes.add(episode);
        return episode;
    }

    public Episode findEpisode(Long episodeId) {
        return episodes.stream()
                .filter(e -> e.getId().equals(episodeId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Episode not found with id: " + episodeId));
    }

    public void removeEpisode(Long episodeId) {
        Episode episode = findEpisode(episodeId);
        episodes.remove(episode);
    }

    public void attachPoster(String posterUrl) {
        this.posterUrl = posterUrl;
    }

    public void refreshImdbRating(BigDecimal imdbRating) {
        this.imdbRating = imdbRating;
    }

    public boolean belongsTo(Long userId) {
        return user != null && user.getId().equals(userId);
    }

    public void update(String title, Integer releaseYear, List<String> genres, String director,
                       BigDecimal imdbRating, BigDecimal personalRating, Integer seasonCount,
                       Integer episodeCount, String platform, String watchUrl,
                       SerialStatus serialStatus, WatchStatus watchStatus,
                       String personalNote, String language, String country) {
        validateRating(personalRating);
        this.title = title;
        this.releaseYear = releaseYear;
        this.genres = genres != null ? new ArrayList<>(genres) : new ArrayList<>();
        this.director = director;
        this.imdbRating = imdbRating;
        this.personalRating = personalRating;
        this.seasonCount = seasonCount;
        this.episodeCount = episodeCount;
        this.platform = platform;
        this.watchUrl = watchUrl;
        if (serialStatus != null) {
            this.serialStatus = serialStatus;
        }
        if (watchStatus != null) {
            this.watchStatus = watchStatus;
        }
        this.personalNote = personalNote;
        this.language = language;
        this.country = country;
    }

    private void validateRating(BigDecimal rating) {
        if (rating != null && (rating.compareTo(MIN_RATING) < 0 || rating.compareTo(MAX_RATING) > 0)) {
            throw new IllegalArgumentException("Rating 0.0 va 10.0 oralig'ida bo'lishi kerak");
        }
    }
}
