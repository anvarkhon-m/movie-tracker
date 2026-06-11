package com.movietracker.repository;

import com.movietracker.TestcontainersConfiguration;
import com.movietracker.domain.Movie;
import com.movietracker.domain.Serial;
import com.movietracker.domain.User;
import com.movietracker.domain.WatchStatus;
import com.movietracker.repository.projection.GenreCount;
import com.movietracker.repository.projection.StatusCount;
import com.movietracker.repository.projection.YearCount;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
class StatsQueriesTest {

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private SerialRepository serialRepository;

    @Autowired
    private UserRepository userRepository;

    private Long userId;

    @BeforeEach
    void setUp() {
        serialRepository.deleteAll();
        movieRepository.deleteAll();
        userRepository.deleteAll();

        User user = userRepository.save(User.builder()
                .googleId("stats-test").email("stats@test.dev").name("Stats").build());
        userId = user.getId();

        Movie completed = Movie.builder()
                .user(user).title("Inception")
                .genres(List.of("Action", "Sci-Fi"))
                .personalRating(new BigDecimal("8.0"))
                .build();
        completed.recordWatch(LocalDate.of(2024, 5, 1), "Netflix", null);
        completed.recordWatch(LocalDate.of(2025, 2, 1), "Netflix", null);

        Movie planned = Movie.builder()
                .user(user).title("Dune")
                .genres(List.of("Action", "Drama"))
                .personalRating(new BigDecimal("6.0"))
                .status(WatchStatus.PLAN_TO_WATCH)
                .build();

        movieRepository.saveAll(List.of(completed, planned));

        serialRepository.save(Serial.builder()
                .user(user).title("Breaking Bad")
                .genres(List.of("Drama", "Crime"))
                .watchStatus(WatchStatus.COMPLETED)
                .personalRating(new BigDecimal("9.0"))
                .build());
    }

    @Test
    void countsMoviesAndSerialsByUser() {
        assertThat(movieRepository.countByUserId(userId)).isEqualTo(2);
        assertThat(serialRepository.countByUserId(userId)).isEqualTo(1);
    }

    @Test
    void groupsMoviesByStatus() {
        Map<WatchStatus, Long> byStatus = movieRepository.countGroupedByStatus(userId).stream()
                .collect(Collectors.toMap(StatusCount::getStatus, StatusCount::getCount));
        assertThat(byStatus).containsEntry(WatchStatus.COMPLETED, 1L)
                .containsEntry(WatchStatus.PLAN_TO_WATCH, 1L);
    }

    @Test
    void unnestsGenreDistribution() {
        Map<String, Long> genres = movieRepository.genreDistribution(userId).stream()
                .collect(Collectors.toMap(GenreCount::getGenre, GenreCount::getCount));
        assertThat(genres).containsEntry("Action", 2L)
                .containsEntry("Sci-Fi", 1L)
                .containsEntry("Drama", 1L);
    }

    @Test
    void averagesPersonalRating() {
        assertThat(movieRepository.averagePersonalRating(userId)).isEqualTo(7.0);
    }

    @Test
    void countsWatchHistoryByYear() {
        Map<Integer, Long> byYear = movieRepository.watchCountByYear(userId).stream()
                .collect(Collectors.toMap(YearCount::getYear, YearCount::getCount));
        assertThat(byYear).containsEntry(2024, 1L).containsEntry(2025, 1L);
    }
}
