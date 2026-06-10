package com.movietracker.repository;

import com.movietracker.domain.EpisodeWatchHistory;
import com.movietracker.domain.Serial;
import com.movietracker.dto.SerialFilter;
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

public final class SerialSpecifications {

    private SerialSpecifications() {
    }

    public static Specification<Serial> withFilter(Long userId, SerialFilter filter) {
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
            if (StringUtils.hasText(filter.language())) {
                predicates.add(cb.equal(cb.lower(root.get("language")), filter.language().toLowerCase()));
            }
            if (StringUtils.hasText(filter.country())) {
                predicates.add(cb.equal(cb.lower(root.get("country")), filter.country().toLowerCase()));
            }
            if (filter.status() != null) {
                predicates.add(cb.equal(root.get("watchStatus"), filter.status()));
            }
            if (filter.watchedYearFrom() != null || filter.watchedYearTo() != null) {
                predicates.add(episodeWatchedBetween(root, query, cb,
                        filter.watchedYearFrom(), filter.watchedYearTo()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private static Predicate episodeWatchedBetween(Root<Serial> root, CriteriaQuery<?> query, CriteriaBuilder cb,
                                                   Integer yearFrom, Integer yearTo) {
        Subquery<Long> subquery = query.subquery(Long.class);
        Root<EpisodeWatchHistory> history = subquery.from(EpisodeWatchHistory.class);
        subquery.select(cb.literal(1L));

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.equal(history.get("episode").get("serial"), root));
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
