package com.movietracker.repository;

import com.movietracker.domain.Episode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EpisodeRepository extends JpaRepository<Episode, Long> {

    Optional<Episode> findByIdAndSerialUserId(Long id, Long userId);

    List<Episode> findBySerialIdOrderBySeasonNoAscEpisodeNoAsc(Long serialId);
}
