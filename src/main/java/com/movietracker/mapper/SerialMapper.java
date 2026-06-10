package com.movietracker.mapper;

import com.movietracker.domain.Serial;
import com.movietracker.domain.User;
import com.movietracker.dto.SerialRequest;
import com.movietracker.dto.SerialResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SerialMapper {

    SerialResponse toResponse(Serial serial);

    List<SerialResponse> toResponseList(List<Serial> serials);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", source = "user")
    @Mapping(target = "tmdbId", source = "request.tmdbId")
    @Mapping(target = "watchStatus", source = "request.watchStatus", defaultValue = "PLAN_TO_WATCH")
    @Mapping(target = "serialStatus", source = "request.serialStatus", defaultValue = "ONGOING")
    @Mapping(target = "genres", source = "request.genres", defaultExpression = "java(new java.util.ArrayList<>())")
    @Mapping(target = "episodes", ignore = true)
    @Mapping(target = "posterUrl", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Serial toEntity(SerialRequest request, User user);
}
