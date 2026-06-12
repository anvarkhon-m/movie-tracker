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
import org.hibernate.annotations.Formula;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "movie")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Movie {

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

    @Column(name = "duration_min")
    private Integer durationMin;

    private String platform;

    @Column(name = "watch_url")
    private String watchUrl;

    @Column(name = "watch_count", nullable = false)
    @Builder.Default
    private int watchCount = 0;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(columnDefinition = "watch_status", nullable = false)
    @Builder.Default
    private WatchStatus status = WatchStatus.PLAN_TO_WATCH;

    @Column(name = "personal_note")
    private String personalNote;

    @Column(columnDefinition = "text")
    private String overview;

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

    @Column(name = "imdb_rating_updated_at")
    private LocalDateTime imdbRatingUpdatedAt;

    // Oxirgi ko'rilgan sana — watch history dan hisoblanadi (ro'yxat so'rovi bilan yuklanadi).
    @Formula("(select max(mwh.watched_at) from movie_watch_history mwh where mwh.movie_id = id)")
    private LocalDate lastWatchedAt;

    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<MovieWatchHistory> watchHistory = new ArrayList<>();

    // --- Domain logika ---

    public void markAsWatched() {
        this.status = WatchStatus.COMPLETED;
        this.watchCount++;
    }

    public void startWatching() {
        this.status = WatchStatus.WATCHING;
    }

    public boolean canBeDropped() {
        return status == WatchStatus.WATCHING;
    }

    public void drop() {
        if (!canBeDropped()) {
            throw new IllegalStateException("Faqat WATCHING holatdagi kino DROPPED qilinadi");
        }
        this.status = WatchStatus.DROPPED;
    }

    public boolean isHighRated() {
        return imdbRating != null && imdbRating.compareTo(HIGH_RATING_THRESHOLD) > 0;
    }

    public void rate(BigDecimal rating) {
        validateRating(rating);
        this.personalRating = rating;
    }

    public MovieWatchHistory recordWatch(LocalDate watchedAt, String platform, String note) {
        MovieWatchHistory entry = MovieWatchHistory.builder()
                .movie(this)
                .watchedAt(watchedAt)
                .platform(platform)
                .note(note)
                .build();
        watchHistory.add(entry);
        this.watchCount++;
        this.status = WatchStatus.COMPLETED;
        return entry;
    }

    public void removeWatchEntry(Long historyId) {
        boolean removed = watchHistory.removeIf(entry -> entry.getId().equals(historyId));
        if (!removed) {
            throw new ResourceNotFoundException("Watch history not found with id: " + historyId);
        }
        if (watchCount > 0) {
            this.watchCount--;
        }
    }

    public void attachPoster(String posterUrl) {
        this.posterUrl = posterUrl;
    }

    public void refreshImdbRating(BigDecimal imdbRating) {
        this.imdbRating = imdbRating;
        this.imdbRatingUpdatedAt = LocalDateTime.now();
    }

    public boolean isImdbRatingFresh(Duration cooldown) {
        return imdbRatingUpdatedAt != null
                && imdbRatingUpdatedAt.isAfter(LocalDateTime.now().minus(cooldown));
    }

    public boolean belongsTo(Long userId) {
        return user != null && user.getId().equals(userId);
    }

    public void update(String title, Integer releaseYear, List<String> genres, String director,
                       BigDecimal imdbRating, BigDecimal personalRating, Integer durationMin,
                       String platform, String watchUrl, WatchStatus status,
                       String personalNote, String language, String country) {
        validateRating(personalRating);
        this.title = title;
        this.releaseYear = releaseYear;
        this.genres = genres != null ? new ArrayList<>(genres) : new ArrayList<>();
        this.director = director;
        this.imdbRating = imdbRating;
        this.personalRating = personalRating;
        this.durationMin = durationMin;
        this.platform = platform;
        this.watchUrl = watchUrl;
        if (status != null) {
            this.status = status;
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
