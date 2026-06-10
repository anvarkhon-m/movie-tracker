package com.movietracker.infrastructure.tmdb;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * TMDB API dan keladigan xom (raw) javoblar.
 * Tashqi qatlamlarga chiqmaydi — TmdbFacade ularni dto ga o'giradi.
 */
public final class TmdbApiResponses {

    private TmdbApiResponses() {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record MovieSearchResponse(List<MovieResult> results) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record MovieResult(
            Integer id,
            String title,
            @JsonProperty("release_date") String releaseDate,
            @JsonProperty("poster_path") String posterPath,
            @JsonProperty("vote_average") Double voteAverage,
            String overview
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record TvSearchResponse(List<TvResult> results) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record TvResult(
            Integer id,
            String name,
            @JsonProperty("first_air_date") String firstAirDate,
            @JsonProperty("poster_path") String posterPath,
            @JsonProperty("vote_average") Double voteAverage,
            String overview
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Genre(Integer id, String name) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record CrewMember(String name, String job) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Credits(List<CrewMember> crew) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Creator(String name) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record ProductionCountry(
            @JsonProperty("iso_3166_1") String iso,
            String name
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record SpokenLanguage(
            @JsonProperty("english_name") String englishName
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record MovieDetailsResponse(
            Integer id,
            String title,
            @JsonProperty("release_date") String releaseDate,
            Integer runtime,
            List<Genre> genres,
            @JsonProperty("poster_path") String posterPath,
            @JsonProperty("vote_average") Double voteAverage,
            @JsonProperty("spoken_languages") List<SpokenLanguage> spokenLanguages,
            @JsonProperty("production_countries") List<ProductionCountry> productionCountries,
            String overview,
            Credits credits
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record TvDetailsResponse(
            Integer id,
            String name,
            @JsonProperty("first_air_date") String firstAirDate,
            @JsonProperty("number_of_seasons") Integer numberOfSeasons,
            @JsonProperty("number_of_episodes") Integer numberOfEpisodes,
            String status,
            List<Genre> genres,
            @JsonProperty("poster_path") String posterPath,
            @JsonProperty("vote_average") Double voteAverage,
            @JsonProperty("spoken_languages") List<SpokenLanguage> spokenLanguages,
            @JsonProperty("production_countries") List<ProductionCountry> productionCountries,
            String overview,
            @JsonProperty("created_by") List<Creator> createdBy,
            Credits credits
    ) {
    }
}
