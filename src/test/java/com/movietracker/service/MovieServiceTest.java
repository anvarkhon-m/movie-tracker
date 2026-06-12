package com.movietracker.service;

import com.movietracker.TestcontainersConfiguration;
import com.movietracker.domain.User;
import com.movietracker.domain.WatchStatus;
import com.movietracker.dto.MovieFilter;
import com.movietracker.dto.MovieRequest;
import com.movietracker.dto.MovieResponse;
import com.movietracker.dto.TmdbMovieDetails;
import com.movietracker.dto.WatchHistoryRequest;
import com.movietracker.exception.ResourceNotFoundException;
import com.movietracker.facade.TmdbFacade;
import com.movietracker.infrastructure.minio.PosterStorageService;
import com.movietracker.repository.MovieRepository;
import com.movietracker.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
class MovieServiceTest {

    @Autowired
    private MovieService movieService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MovieRepository movieRepository;
    @Autowired
    private JdbcTemplate jdbc;

    @MockitoBean
    private TmdbFacade tmdbFacade;
    @MockitoBean
    private PosterStorageService posterStorageService;

    private Long userA;
    private Long userB;

    @BeforeEach
    void setUp() {
        jdbc.execute("TRUNCATE TABLE episode_watch_history, movie_watch_history, episode, "
                + "movie, serial, users RESTART IDENTITY CASCADE");
        userA = userRepository.save(User.builder().googleId("a").email("a@x.com").name("A").build()).getId();
        userB = userRepository.save(User.builder().googleId("b").email("b@x.com").name("B").build()).getId();
    }

    @AfterEach
    void clear() {
        SecurityContextHolder.clearContext();
    }

    private void authenticate(Long userId) {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(userId, null, List.of()));
    }

    private MovieRequest manual(String title, WatchStatus status) {
        return new MovieRequest(null, title, 2010, List.of("Sci-Fi"), "Nolan",
                new BigDecimal("8.8"), null, 148, "Netflix", null, status, null, "English", "USA");
    }

    private MovieFilter emptyFilter() {
        return new MovieFilter(null, null, null, null, null, null, null, null, null, null, null, null, null);
    }

    @Test
    void createsManualMovie() {
        authenticate(userA);
        MovieResponse r = movieService.create(manual("Inception", WatchStatus.PLAN_TO_WATCH));
        assertThat(r.id()).isNotNull();
        assertThat(r.title()).isEqualTo("Inception");
        assertThat(movieRepository.findById(r.id())).isPresent();
    }

    @Test
    void enforcesOwnershipIsolation() {
        authenticate(userA);
        MovieResponse mine = movieService.create(manual("Mine", WatchStatus.PLAN_TO_WATCH));

        authenticate(userB);
        assertThatThrownBy(() -> movieService.get(mine.id()))
                .isInstanceOf(ResourceNotFoundException.class);
        Page<MovieResponse> bList = movieService.list(emptyFilter(), PageRequest.of(0, 20));
        assertThat(bList.getContent()).isEmpty();
    }

    @Test
    void recordsWatchHistoryAndUpdatesStatus() {
        authenticate(userA);
        MovieResponse m = movieService.create(manual("X", WatchStatus.PLAN_TO_WATCH));
        movieService.addHistory(m.id(), new WatchHistoryRequest(LocalDate.of(2025, 1, 1), "Netflix", null));

        MovieResponse after = movieService.get(m.id());
        assertThat(after.watchCount()).isEqualTo(1);
        assertThat(after.status()).isEqualTo(WatchStatus.COMPLETED);
    }

    @Test
    void filtersByStatus() {
        authenticate(userA);
        movieService.create(manual("Done", WatchStatus.COMPLETED));
        movieService.create(manual("Planned", WatchStatus.PLAN_TO_WATCH));

        MovieFilter filter = new MovieFilter(null, null, null, null, null, null, null, null, null, null,
                null, null, WatchStatus.COMPLETED);
        Page<MovieResponse> page = movieService.list(filter, PageRequest.of(0, 20));
        assertThat(page.getContent()).extracting(MovieResponse::title).containsExactly("Done");
    }

    @Test
    void createsFromTmdbAndKeepsUserWatchedLanguage() {
        authenticate(userA);
        TmdbMovieDetails details = new TmdbMovieDetails(27205, "Inception", 2010, List.of("Sci-Fi"),
                "Nolan", new BigDecimal("8.8"), 148, null, null, "English", "USA", "A thief...");
        when(tmdbFacade.getMovieDetails(27205)).thenReturn(details);

        MovieRequest req = new MovieRequest(27205, null, null, null, null, null, null, null,
                "Netflix", null, null, null, "Uzbek", null);
        MovieResponse r = movieService.create(req);

        assertThat(r.title()).isEqualTo("Inception");
        assertThat(r.overview()).isEqualTo("A thief...");
        assertThat(r.imdbRating()).isEqualByComparingTo("8.8");
        assertThat(r.language()).isEqualTo("Uzbek");
    }
}
