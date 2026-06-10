package com.movietracker.mapper;

import com.movietracker.domain.EpisodeWatchHistory;
import com.movietracker.domain.MovieWatchHistory;
import com.movietracker.dto.WatchHistoryResponse;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface WatchHistoryMapper {

    WatchHistoryResponse toResponse(MovieWatchHistory history);

    List<WatchHistoryResponse> toMovieHistoryResponseList(List<MovieWatchHistory> history);

    WatchHistoryResponse toResponse(EpisodeWatchHistory history);

    List<WatchHistoryResponse> toEpisodeHistoryResponseList(List<EpisodeWatchHistory> history);
}
