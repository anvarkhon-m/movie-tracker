package com.movietracker.facade;

import com.movietracker.config.TmdbProperties;
import com.movietracker.domain.SerialStatus;
import com.movietracker.dto.TmdbMovieDetails;
import com.movietracker.dto.TmdbMovieSummary;
import com.movietracker.dto.TmdbSerialDetails;
import com.movietracker.dto.TmdbSerialSummary;
import com.movietracker.infrastructure.tmdb.TmdbApiResponses.Credits;
import com.movietracker.infrastructure.tmdb.TmdbApiResponses.Creator;
import com.movietracker.infrastructure.tmdb.TmdbApiResponses.Genre;
import com.movietracker.infrastructure.tmdb.TmdbApiResponses.MovieDetailsResponse;
import com.movietracker.infrastructure.tmdb.TmdbApiResponses.ProductionCountry;
import com.movietracker.infrastructure.tmdb.TmdbApiResponses.SpokenLanguage;
import com.movietracker.infrastructure.tmdb.TmdbApiResponses.TvDetailsResponse;
import com.movietracker.infrastructure.tmdb.TmdbClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * TMDB API murakkabligini yashiradi — tashqi qatlamlar faqat
 * shu facade orqali TMDB bilan ishlaydi.
 */
@Service
@RequiredArgsConstructor
public class TmdbFacade {

    private static final String DIRECTOR_JOB = "Director";
    private static final int RELEASE_YEAR_LENGTH = 4;

    private final TmdbClient tmdbClient;
    private final TmdbProperties properties;
    private final com.movietracker.infrastructure.omdb.OmdbClient omdbClient;

    public List<TmdbMovieSummary> searchMovies(String query) {
        return tmdbClient.searchMovies(query).results().stream()
                .map(result -> new TmdbMovieSummary(
                        result.id(),
                        result.title(),
                        parseYear(result.releaseDate()),
                        posterUrl(result.posterPath()),
                        toRating(result.voteAverage()),
                        result.overview()))
                .toList();
    }

    public List<TmdbSerialSummary> searchSerials(String query) {
        return tmdbClient.searchTv(query).results().stream()
                .map(result -> new TmdbSerialSummary(
                        result.id(),
                        result.name(),
                        parseYear(result.firstAirDate()),
                        posterUrl(result.posterPath()),
                        toRating(result.voteAverage()),
                        result.overview()))
                .toList();
    }

    public TmdbMovieDetails getMovieDetails(int tmdbId) {
        MovieDetailsResponse details = tmdbClient.getMovieDetails(tmdbId);
        return new TmdbMovieDetails(
                details.id(),
                details.title(),
                parseYear(details.releaseDate()),
                genreNames(details.genres()),
                director(details.credits()),
                imdbRating(details.imdbId(), details.voteAverage()),
                details.runtime(),
                details.posterPath(),
                posterUrl(details.posterPath()),
                language(details.spokenLanguages()),
                country(details.productionCountries()),
                details.overview());
    }

    public TmdbSerialDetails getSerialDetails(int tmdbId) {
        TvDetailsResponse details = tmdbClient.getTvDetails(tmdbId);
        return new TmdbSerialDetails(
                details.id(),
                details.name(),
                parseYear(details.firstAirDate()),
                genreNames(details.genres()),
                creator(details.createdBy(), details.credits()),
                imdbRating(imdbId(details.externalIds()), details.voteAverage()),
                details.numberOfSeasons(),
                details.numberOfEpisodes(),
                toSerialStatus(details.status()),
                details.posterPath(),
                posterUrl(details.posterPath()),
                language(details.spokenLanguages()),
                country(details.productionCountries()),
                details.overview());
    }

    public byte[] downloadPoster(String posterPath) {
        return tmdbClient.downloadImage(posterPath);
    }

    private Integer parseYear(String date) {
        if (!StringUtils.hasText(date) || date.length() < RELEASE_YEAR_LENGTH) {
            return null;
        }
        try {
            return Integer.parseInt(date.substring(0, RELEASE_YEAR_LENGTH));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private BigDecimal toRating(Double voteAverage) {
        if (voteAverage == null) {
            return null;
        }
        return BigDecimal.valueOf(voteAverage).setScale(1, RoundingMode.HALF_UP);
    }

    /**
     * Haqiqiy IMDb reytingi (OMDb orqali). Topilmasa — TMDB vote_average ga fallback.
     */
    private BigDecimal imdbRating(String imdbId, Double voteAverage) {
        return omdbClient.getImdbRating(imdbId).orElseGet(() -> toRating(voteAverage));
    }

    private String imdbId(com.movietracker.infrastructure.tmdb.TmdbApiResponses.ExternalIds externalIds) {
        return externalIds != null ? externalIds.imdbId() : null;
    }

    private String posterUrl(String posterPath) {
        return posterPath != null ? properties.imageBaseUrl() + posterPath : null;
    }

    private List<String> genreNames(List<Genre> genres) {
        return genres == null ? List.of() : genres.stream().map(Genre::name).toList();
    }

    private String director(Credits credits) {
        if (credits == null || credits.crew() == null) {
            return null;
        }
        return credits.crew().stream()
                .filter(member -> DIRECTOR_JOB.equals(member.job()))
                .map(member -> member.name())
                .findFirst()
                .orElse(null);
    }

    private String creator(List<Creator> createdBy, Credits credits) {
        if (createdBy != null && !createdBy.isEmpty()) {
            return createdBy.getFirst().name();
        }
        return director(credits);
    }

    private SerialStatus toSerialStatus(String status) {
        if (status == null) {
            return SerialStatus.ONGOING;
        }
        return switch (status) {
            case "Ended" -> SerialStatus.ENDED;
            case "Canceled", "Cancelled" -> SerialStatus.CANCELLED;
            default -> SerialStatus.ONGOING;
        };
    }

    private String language(List<SpokenLanguage> languages) {
        return languages == null || languages.isEmpty() ? null : languages.getFirst().englishName();
    }

    private String country(List<ProductionCountry> countries) {
        return countries == null || countries.isEmpty() ? null : countries.getFirst().name();
    }
}
