package com.movietracker.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "omdb")
public record OmdbProperties(
        String apiKey,
        String baseUrl
) {
}
