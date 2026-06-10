package com.movietracker.repository;

import com.movietracker.domain.Movie;
import com.movietracker.domain.MovieWatchHistory;
import com.movietracker.dto.MovieFilter;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public final class MovieSpecifications {

    private MovieSpecifications() {
    }

    public static Specification<Movie> withFilter(Long userId, MovieFilter filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("user").get("id"), userId));

            if (StringUtils.hasText(filter.search())) {
                predicates.add(cb.like(cb.lower(root.get("title")),
                        "%" + filter.search().toLowerCase() + "%"));
            }
            if (filter.releaseYearFrom() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("releaseYear"), filter.releaseYearFrom()));
            }
            if (filter.releaseYearTo() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("releaseYear"), filter.releaseYearTo()));
            }
            if (StringUtils.hasText(filter.genre())) {
                predicates.add(cb.isTrue(cb.function("array_contains", Boolean.class,
                        root.get("genres"), cb.literal(filter.genre()))));
            }
            if (filter.minRating() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("imdbRating"), filter.minRating()));
            }
            if (filter.maxRating() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("imdbRating"), filter.maxRating()));
            }
            if (filter.minDuration() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("durationMin"), filter.minDuration()));
            }
            if (filter.maxDuration() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("durationMin"), filter.maxDuration()));
            }
            if (StringUtils.hasText(filter.language())) {
                predicates.add(cb.equal(cb.lower(root.get("language")), filter.language().toLowerCase()));
            }
            if (StringUtils.hasText(filter.country())) {
                predicates.add(cb.equal(cb.lower(root.get("country")), filter.country().toLowerCase()));
            }
            if (filter.status() != null) {
                predicates.add(cb.equal(root.get("status"), filter.status()));
            }
            if (filter.watchedYearFrom() != null || filter.watchedYearTo() != null) {
                predicates.add(watchedBetween(root, query, cb,
                        filter.watchedYearFrom(), filter.watchedYearTo()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private static Predicate watchedBetween(Root<Movie> root, CriteriaQuery<?> query, CriteriaBuilder cb,
                                            Integer yearFrom, Integer yearTo) {
        Subquery<Long> subquery = query.subquery(Long.class);
        Root<MovieWatchHistory> history = subquery.from(MovieWatchHistory.class);
        subquery.select(cb.literal(1L));

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.equal(history.get("movie"), root));
        if (yearFrom != null) {
            predicates.add(cb.greaterThanOrEqualTo(history.get("watchedAt"), LocalDate.of(yearFrom, 1, 1)));
        }
        if (yearTo != null) {
            predicates.add(cb.lessThanOrEqualTo(history.get("watchedAt"), LocalDate.of(yearTo, 12, 31)));
        }
        subquery.where(predicates.toArray(new Predicate[0]));

        return cb.exists(subquery);
    }
}
