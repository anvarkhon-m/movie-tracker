package com.movietracker.service;

import com.movietracker.domain.WatchStatus;
import com.movietracker.dto.StatsResponse;
import com.movietracker.dto.StatsResponse.GenreStat;
import com.movietracker.repository.MovieRepository;
import com.movietracker.repository.SerialRepository;
import com.movietracker.repository.projection.GenreCount;
import com.movietracker.repository.projection.StatusCount;
import com.movietracker.repository.projection.YearCount;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class StatsService {

    private static final int TOP_GENRES_LIMIT = 10;
    private static final int RATING_SCALE = 1;

    private final MovieRepository movieRepository;
    private final SerialRepository serialRepository;
    private final AuthService authService;

    @Transactional(readOnly = true)
    public StatsResponse getStats() {
        Long userId = authService.currentUserId();
        return new StatsResponse(
                movieRepository.countByUserId(userId),
                serialRepository.countByUserId(userId),
                toStatusMap(movieRepository.countGroupedByStatus(userId)),
                toStatusMap(serialRepository.countGroupedByStatus(userId)),
                topGenres(movieRepository.genreDistribution(userId),
                        serialRepository.genreDistribution(userId)),
                round(movieRepository.averagePersonalRating(userId)),
                round(serialRepository.averagePersonalRating(userId)),
                toYearMap(movieRepository.watchCountByYear(userId)));
    }

    private Map<WatchStatus, Long> toStatusMap(List<StatusCount> counts) {
        Map<WatchStatus, Long> map = new EnumMap<>(WatchStatus.class);
        for (WatchStatus status : WatchStatus.values()) {
            map.put(status, 0L);
        }
        counts.forEach(c -> map.put(c.getStatus(), c.getCount()));
        return map;
    }

    private List<GenreStat> topGenres(List<GenreCount> movieGenres, List<GenreCount> serialGenres) {
        Map<String, Long> merged = Stream.concat(movieGenres.stream(), serialGenres.stream())
                .collect(Collectors.groupingBy(GenreCount::getGenre,
                        Collectors.summingLong(GenreCount::getCount)));
        return merged.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed()
                        .thenComparing(Map.Entry.comparingByKey()))
                .limit(TOP_GENRES_LIMIT)
                .map(e -> new GenreStat(e.getKey(), e.getValue()))
                .toList();
    }

    private Map<Integer, Long> toYearMap(List<YearCount> counts) {
        return counts.stream()
                .collect(Collectors.toMap(YearCount::getYear, YearCount::getCount,
                        (a, b) -> a, LinkedHashMap::new));
    }

    private BigDecimal round(Double value) {
        if (value == null) {
            return null;
        }
        return BigDecimal.valueOf(value).setScale(RATING_SCALE, RoundingMode.HALF_UP);
    }
}
