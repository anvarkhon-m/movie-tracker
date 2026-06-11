package com.movietracker.infrastructure.omdb;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.movietracker.config.OmdbProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

/**
 * OMDb API — TMDB bermaydigan haqiqiy IMDb reytingini imdb_id orqali oladi.
 * Kalit bo'lmasa yoki reyting topilmasa — bo'sh Optional qaytaradi (TMDB ga fallback).
 */
@Slf4j
@Component
public class OmdbClient {

    private static final String NOT_AVAILABLE = "N/A";

    private final RestClient restClient;
    private final OmdbProperties properties;

    public OmdbClient(OmdbProperties properties, RestClient.Builder builder) {
        this.properties = properties;
        this.restClient = builder.baseUrl(properties.baseUrl()).build();
    }

    public Optional<BigDecimal> getImdbRating(String imdbId) {
        if (!StringUtils.hasText(imdbId) || !StringUtils.hasText(properties.apiKey())) {
            return Optional.empty();
        }
        try {
            OmdbResponse response = restClient.get()
                    .uri(uri -> uri.queryParam("apikey", properties.apiKey())
                            .queryParam("i", imdbId)
                            .build())
                    .retrieve()
                    .body(OmdbResponse.class);
            if (response == null || !StringUtils.hasText(response.imdbRating())
                    || NOT_AVAILABLE.equalsIgnoreCase(response.imdbRating())) {
                return Optional.empty();
            }
            return Optional.of(new BigDecimal(response.imdbRating()).setScale(1, RoundingMode.HALF_UP));
        } catch (Exception e) {
            log.warn("OMDb IMDb rating fetch failed for {}", imdbId, e);
            return Optional.empty();
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record OmdbResponse(@JsonProperty("imdbRating") String imdbRating) {
    }
}
