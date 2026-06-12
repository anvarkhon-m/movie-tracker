package com.movietracker.repository;

import com.movietracker.domain.Movie;
import com.movietracker.repository.projection.GenreCount;
import com.movietracker.repository.projection.StatusCount;
import com.movietracker.repository.projection.YearCount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MovieRepository extends JpaRepository<Movie, Long>, JpaSpecificationExecutor<Movie> {

    Optional<Movie> findByIdAndUserId(Long id, Long userId);

    long countByUserId(Long userId);

    @Query("SELECT m.tmdbId FROM Movie m WHERE m.user.id = :userId AND m.tmdbId IS NOT NULL")
    List<Integer> findTmdbIdsByUserId(@Param("userId") Long userId);

    @Query("SELECT m.status AS status, COUNT(m) AS count FROM Movie m "
            + "WHERE m.user.id = :userId GROUP BY m.status")
    List<StatusCount> countGroupedByStatus(@Param("userId") Long userId);

    @Query(value = "SELECT g AS genre, COUNT(*) AS count FROM movie m, unnest(m.genres) AS g "
            + "WHERE m.user_id = :userId GROUP BY g", nativeQuery = true)
    List<GenreCount> genreDistribution(@Param("userId") Long userId);

    @Query("SELECT AVG(m.personalRating) FROM Movie m "
            + "WHERE m.user.id = :userId AND m.personalRating IS NOT NULL")
    Double averagePersonalRating(@Param("userId") Long userId);

    @Query(value = "SELECT EXTRACT(YEAR FROM h.watched_at)::int AS year, COUNT(*) AS count "
            + "FROM movie_watch_history h JOIN movie m ON m.id = h.movie_id "
            + "WHERE m.user_id = :userId GROUP BY year ORDER BY year", nativeQuery = true)
    List<YearCount> watchCountByYear(@Param("userId") Long userId);
}
