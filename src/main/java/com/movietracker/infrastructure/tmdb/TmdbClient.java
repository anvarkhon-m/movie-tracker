package com.movietracker.infrastructure.tmdb;

import com.movietracker.config.TmdbProperties;
import com.movietracker.exception.ResourceNotFoundException;
import com.movietracker.exception.TmdbException;
import com.movietracker.infrastructure.tmdb.TmdbApiResponses.MovieDetailsResponse;
import com.movietracker.infrastructure.tmdb.TmdbApiResponses.MovieSearchResponse;
import com.movietracker.infrastructure.tmdb.TmdbApiResponses.TvDetailsResponse;
import com.movietracker.infrastructure.tmdb.TmdbApiResponses.TvSearchResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
public class TmdbClient {

    private static final String API_KEY_PARAM = "api_key";

    private final RestClient restClient;
    private final RestClient imageClient;
    private final TmdbProperties properties;

    public TmdbClient(TmdbProperties properties, RestClient.Builder builder) {
        this.properties = properties;
        this.restClient = builder.baseUrl(properties.baseUrl()).build();
        this.imageClient = RestClient.create();
    }

    public MovieSearchResponse searchMovies(String query) {
        try {
            return restClient.get()
                    .uri(uri -> uri.path("/search/movie")
                            .queryParam("query", query)
                            .queryParam(API_KEY_PARAM, properties.apiKey())
                            .build())
                    .retrieve()
                    .body(MovieSearchResponse.class);
        } catch (RestClientException e) {
            throw new TmdbException("TMDB movie search failed", e);
        }
    }

    public TvSearchResponse searchTv(String query) {
        try {
            return restClient.get()
                    .uri(uri -> uri.path("/search/tv")
                            .queryParam("query", query)
                            .queryParam(API_KEY_PARAM, properties.apiKey())
                            .build())
                    .retrieve()
                    .body(TvSearchResponse.class);
        } catch (RestClientException e) {
            throw new TmdbException("TMDB serial search failed", e);
        }
    }

    public MovieDetailsResponse getMovieDetails(int tmdbId) {
        try {
            return restClient.get()
                    .uri(uri -> uri.path("/movie/{id}")
                            .queryParam("append_to_response", "credits")
                            .queryParam(API_KEY_PARAM, properties.apiKey())
                            .build(tmdbId))
                    .retrieve()
                    .body(MovieDetailsResponse.class);
        } catch (HttpClientErrorException.NotFound e) {
            throw new ResourceNotFoundException("TMDB movie not found with id: " + tmdbId);
        } catch (RestClientException e) {
            throw new TmdbException("TMDB movie details failed for id: " + tmdbId, e);
        }
    }

    public TvDetailsResponse getTvDetails(int tmdbId) {
        try {
            return restClient.get()
                    .uri(uri -> uri.path("/tv/{id}")
                            .queryParam("append_to_response", "credits,external_ids")
                            .queryParam(API_KEY_PARAM, properties.apiKey())
                            .build(tmdbId))
                    .retrieve()
                    .body(TvDetailsResponse.class);
        } catch (HttpClientErrorException.NotFound e) {
            throw new ResourceNotFoundException("TMDB serial not found with id: " + tmdbId);
        } catch (RestClientException e) {
            throw new TmdbException("TMDB serial details failed for id: " + tmdbId, e);
        }
    }

    public byte[] downloadImage(String posterPath) {
        try {
            return imageClient.get()
                    .uri(properties.imageBaseUrl() + posterPath)
                    .retrieve()
                    .body(byte[].class);
        } catch (RestClientException e) {
            throw new TmdbException("TMDB poster download failed: " + posterPath, e);
        }
    }
}
