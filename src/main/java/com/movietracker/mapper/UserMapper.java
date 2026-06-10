package com.movietracker.mapper;

import com.movietracker.domain.User;
import com.movietracker.dto.UserResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserResponse toResponse(User user);
}
