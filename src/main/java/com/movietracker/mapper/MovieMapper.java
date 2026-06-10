package com.movietracker.mapper;

import com.movietracker.domain.Movie;
import com.movietracker.domain.User;
import com.movietracker.dto.MovieRequest;
import com.movietracker.dto.MovieResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MovieMapper {

    MovieResponse toResponse(Movie movie);

    List<MovieResponse> toResponseList(List<Movie> movies);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", source = "user")
    @Mapping(target = "tmdbId", source = "request.tmdbId")
    @Mapping(target = "status", source = "request.status", defaultValue = "PLAN_TO_WATCH")
    @Mapping(target = "genres", source = "request.genres", defaultExpression = "java(new java.util.ArrayList<>())")
    @Mapping(target = "watchCount", ignore = true)
    @Mapping(target = "watchHistory", ignore = true)
    @Mapping(target = "posterUrl", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Movie toEntity(MovieRequest request, User user);
}
