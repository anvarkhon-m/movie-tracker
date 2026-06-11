package com.movietracker.repository;

import com.movietracker.domain.Serial;
import com.movietracker.repository.projection.GenreCount;
import com.movietracker.repository.projection.StatusCount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SerialRepository extends JpaRepository<Serial, Long>, JpaSpecificationExecutor<Serial> {

    Optional<Serial> findByIdAndUserId(Long id, Long userId);

    long countByUserId(Long userId);

    @Query("SELECT s.watchStatus AS status, COUNT(s) AS count FROM Serial s "
            + "WHERE s.user.id = :userId GROUP BY s.watchStatus")
    List<StatusCount> countGroupedByStatus(@Param("userId") Long userId);

    @Query(value = "SELECT g AS genre, COUNT(*) AS count FROM serial s, unnest(s.genres) AS g "
            + "WHERE s.user_id = :userId GROUP BY g", nativeQuery = true)
    List<GenreCount> genreDistribution(@Param("userId") Long userId);

    @Query("SELECT AVG(s.personalRating) FROM Serial s "
            + "WHERE s.user.id = :userId AND s.personalRating IS NOT NULL")
    Double averagePersonalRating(@Param("userId") Long userId);
}
