package com.movietracker.service;

import com.movietracker.TestcontainersConfiguration;
import com.movietracker.domain.SerialStatus;
import com.movietracker.domain.User;
import com.movietracker.domain.WatchStatus;
import com.movietracker.dto.EpisodeRequest;
import com.movietracker.dto.EpisodeResponse;
import com.movietracker.dto.SerialRequest;
import com.movietracker.dto.SerialResponse;
import com.movietracker.exception.ResourceNotFoundException;
import com.movietracker.facade.TmdbFacade;
import com.movietracker.infrastructure.minio.PosterStorageService;
import com.movietracker.repository.SerialRepository;
import com.movietracker.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
class SerialServiceTest {

    @Autowired
    private SerialService serialService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SerialRepository serialRepository;
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

    private SerialRequest manual(String title) {
        return new SerialRequest(null, title, 2008, List.of("Drama"), "Gilligan",
                new BigDecimal("9.0"), null, 5, 62, "Netflix", null,
                SerialStatus.ENDED, WatchStatus.PLAN_TO_WATCH, null, "English", "USA");
    }

    @Test
    void addsEpisodeAndRejectsDuplicate() {
        authenticate(userA);
        SerialResponse s = serialService.create(manual("Breaking Bad"));
        serialService.addEpisode(s.id(), new EpisodeRequest(1, 1, "Pilot", 58, null));

        List<EpisodeResponse> episodes = serialService.getEpisodes(s.id());
        assertThat(episodes).hasSize(1);
        assertThat(episodes.getFirst().title()).isEqualTo("Pilot");

        assertThatThrownBy(() -> serialService.addEpisode(s.id(), new EpisodeRequest(1, 1, "Dup", null, null)))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void enforcesOwnershipIsolation() {
        authenticate(userA);
        SerialResponse mine = serialService.create(manual("Mine"));

        authenticate(userB);
        assertThatThrownBy(() -> serialService.get(mine.id()))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
