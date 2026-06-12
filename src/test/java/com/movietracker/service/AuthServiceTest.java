package com.movietracker.service;

import com.movietracker.TestcontainersConfiguration;
import com.movietracker.domain.User;
import com.movietracker.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
class AuthServiceTest {

    @Autowired
    private AuthService authService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JdbcTemplate jdbc;

    @BeforeEach
    void setUp() {
        jdbc.execute("TRUNCATE TABLE episode_watch_history, movie_watch_history, episode, "
                + "movie, serial, users RESTART IDENTITY CASCADE");
    }

    @Test
    void createsNewUser() {
        User u = authService.findOrCreateUser("g1", "a@b.com", "A", "pic");
        assertThat(u.getId()).isNotNull();
        assertThat(userRepository.findByGoogleId("g1")).isPresent();
    }

    @Test
    void findsExistingByGoogleIdAndUpdatesProfile() {
        User first = authService.findOrCreateUser("g1", "a@b.com", "A", null);
        User second = authService.findOrCreateUser("g1", "a@b.com", "A New", "pic");

        assertThat(second.getId()).isEqualTo(first.getId());
        assertThat(second.getName()).isEqualTo("A New");
        assertThat(userRepository.count()).isEqualTo(1);
    }

    @Test
    void linksGoogleAccountToExistingEmail() {
        User original = authService.findOrCreateUser("g1", "a@b.com", "A", null);
        // Same email, different Google id (e.g. a pre-seeded account) -> link, not duplicate
        User linked = authService.findOrCreateUser("g2", "a@b.com", "A2", null);

        assertThat(linked.getId()).isEqualTo(original.getId());
        assertThat(linked.getGoogleId()).isEqualTo("g2");
        assertThat(userRepository.count()).isEqualTo(1);
    }
}
