package com.movietracker.mapper;

import com.movietracker.domain.Episode;
import com.movietracker.dto.EpisodeResponse;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EpisodeMapper {

    EpisodeResponse toResponse(Episode episode);

    List<EpisodeResponse> toResponseList(List<Episode> episodes);
}
